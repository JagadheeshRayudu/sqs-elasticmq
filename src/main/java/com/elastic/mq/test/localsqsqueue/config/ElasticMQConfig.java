package com.elastic.mq.test.localsqsqueue.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClient;
import com.amazonaws.services.sqs.buffered.AmazonSQSBufferedAsyncClient;
import com.amazonaws.services.sqs.buffered.QueueBufferConfig;
import lombok.RequiredArgsConstructor;
import org.elasticmq.rest.sqs.SQSRestServer;
import org.elasticmq.rest.sqs.SQSRestServerBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Configuration
@EnableConfigurationProperties(SqsConfigMappingProperties.class)
@RequiredArgsConstructor
public class ElasticMQConfig {
    @Value("${aws.local.sqs.localElasticMq.enable}")
    Boolean enableLocalElasticMq;
    @Value("${aws.local.sqs.localElasticMq.startServer}")
    Boolean startLocalElasticMq;
    private final SqsConfigMappingProperties sqsConfigMappingProperties;
    @Value("${spring.profiles.active}")
    private String env;

    @Bean
    public UriComponents elasticMqLocalSqsUri() {
        LocalServiceUri elasticMqLocalSqsUri = sqsConfigMappingProperties.getElasticMqLocalSqsUri();
        return UriComponentsBuilder.newInstance()
                .scheme(elasticMqLocalSqsUri.getScheme())
                .host(elasticMqLocalSqsUri.getHost())
                .port(elasticMqLocalSqsUri.getPort())
                .build()
                .encode();
    }
    @Bean
    public SQSRestServer sqsRestServer(UriComponents elasticMqLocalSqsUri) {
        SQSRestServer sqsRestServer = SQSRestServerBuilder
                .withPort(Integer.valueOf(elasticMqLocalSqsUri.getPort()))
                .withInterface(elasticMqLocalSqsUri.getHost())
                .start();
        return sqsRestServer;
    }
    @Lazy
    @Bean(name = "amazonSqsLocal")
    @DependsOn("sqsRestServer")
    @ConditionalOnExpression("${aws.local.sqs.localElasticMq.enable}")
    public AmazonSQSAsync amazonSqsLocal(AWSCredentials amazonAWSCredentials) {
        AmazonSQSAsyncClient awsSQSAsyncClient = new AmazonSQSAsyncClient(amazonAWSCredentials);
        awsSQSAsyncClient.setEndpoint(createURI(sqsConfigMappingProperties.getElasticMqLocalSqsUri()));
        awsSQSAsyncClient.createQueue(sqsConfigMappingProperties.getAwsConfig().getSqsQueueName());
        QueueBufferMappedProperties queueBufferMappedProperties = sqsConfigMappingProperties.getQueueBuffer();
        QueueBufferConfig config = new QueueBufferConfig()
                .withMaxBatchOpenMs(queueBufferMappedProperties.getMaxBatchOpenMs())
                .withMaxBatchSize(queueBufferMappedProperties.getMaxBatchSize())
                .withMaxInflightOutboundBatches(queueBufferMappedProperties.getMaxInflightOutboundBatches());
        AmazonSQSBufferedAsyncClient amazonSQSBufferedAsyncClient = new AmazonSQSBufferedAsyncClient(awsSQSAsyncClient,config);
        return amazonSQSBufferedAsyncClient;
    }
    @Lazy
    @Bean(name = "amazonSqs")
    @ConditionalOnExpression("!${aws.local.sqs.localElasticMq.enable}")
    public AmazonSQSAsync amazonSqs(AWSCredentials amazonAWSCredentials) {
        AmazonSQSAsyncClient awsSQSAsyncClient = new AmazonSQSAsyncClient(amazonAWSCredentials);
        awsSQSAsyncClient.setEndpoint(createURI(sqsConfigMappingProperties.getAwsSqsUri()));
        awsSQSAsyncClient.createQueue(sqsConfigMappingProperties.getAwsConfig().getSqsQueueName());
        QueueBufferMappedProperties queueBufferMappedProperties = sqsConfigMappingProperties.getQueueBuffer();
        QueueBufferConfig config = new QueueBufferConfig()
                .withMaxBatchOpenMs(queueBufferMappedProperties.getMaxBatchOpenMs())
                .withMaxBatchSize(queueBufferMappedProperties.getMaxBatchSize())
                .withMaxInflightOutboundBatches(queueBufferMappedProperties.getMaxInflightOutboundBatches());
        AmazonSQSBufferedAsyncClient amazonSQSBufferedAsyncClient = new AmazonSQSBufferedAsyncClient(awsSQSAsyncClient,config);
        return amazonSQSBufferedAsyncClient;
    }
    @Bean
    public QueueMessagingTemplate queueMessagingTemplate(AmazonSQSAsync amazonSqs, AmazonSQSAsync amazonSqsLocal, SQSRestServer sqsRestServer) {
        QueueMessagingTemplate queueMessagingTemplate;
        if(enableLocalElasticMq)
            queueMessagingTemplate = new QueueMessagingTemplate(amazonSqsLocal);
        else
            queueMessagingTemplate = new QueueMessagingTemplate(amazonSqs);
        queueMessagingTemplate.setDefaultDestinationName(sqsConfigMappingProperties.getAwsConfig().getSqsQueueName());
        if(!startLocalElasticMq)
            sqsRestServer.stopAndWait();
        return queueMessagingTemplate;
    }
    @Bean
    public AWSCredentials amazonAWSCredentials() {
        if ("local".equals(env)) {
            return new BasicAWSCredentials(sqsConfigMappingProperties.getAwsConfig().getAccessKey(),
                    sqsConfigMappingProperties.getAwsConfig().getSecretKey());
        }
        return new DefaultAWSCredentialsProviderChain().getCredentials();
    }
    private static String createURI(LocalServiceUri localServiceUri) {
        return UriComponentsBuilder.newInstance()
                .scheme(localServiceUri.getScheme())
                .host(localServiceUri.getHost())
                .port(localServiceUri.getPort())
                .path(localServiceUri.getPath())
                .build()
                .encode().toUriString();
    }
}