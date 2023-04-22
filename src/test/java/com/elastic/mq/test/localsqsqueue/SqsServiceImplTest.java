package com.elastic.mq.test.localsqsqueue;

import akka.stream.BindFailedException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQSAsyncClient;
import com.elastic.mq.test.localsqsqueue.config.AwsMappedProperties;
import com.elastic.mq.test.localsqsqueue.config.SqsConfigMappingProperties;
import com.elastic.mq.test.localsqsqueue.service.SqsService;
import org.elasticmq.rest.sqs.SQSRestServer;
import org.elasticmq.rest.sqs.SQSRestServerBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Profile("local")
public class SqsServiceImplTest {
    private static final String QUEUE_NAME = "sqs-queue-name";
    private static final int SQS_PORT = 9324;
    private static final String SQS_HOSTNAME = "localhost";
    private static final String EXPECTED_SELLING_PRICE = "100";
    private SQSRestServer sqsRestServer;
    private SqsService classUnderTest;
    private QueueMessagingTemplate queueMessagingTemplate;
    private SqsConfigMappingProperties sqsMappingProperties;
    @BeforeEach
    public void setUp() {
        //Run local ElasticMQ SQS queue
        try {
            sqsRestServer = SQSRestServerBuilder
                    .withPort(SQS_PORT)
                    .withInterface(SQS_HOSTNAME)
                    .start();
        } catch (BindFailedException e) {
            e.printStackTrace();
        }
        AmazonSQSAsyncClient awsSQSAsyncClient = new AmazonSQSAsyncClient(new BasicAWSCredentials("x", "x"));
        awsSQSAsyncClient.setEndpoint("http://" + SQS_HOSTNAME + ":" + SQS_PORT);
        awsSQSAsyncClient.createQueue(QUEUE_NAME);
        SqsConfigMappingProperties sqsMappingProperties = new SqsConfigMappingProperties();
        sqsMappingProperties.setAwsConfig(buildAwsConfig());
        queueMessagingTemplate = new QueueMessagingTemplate(awsSQSAsyncClient);
        queueMessagingTemplate.setDefaultDestinationName(QUEUE_NAME);
        classUnderTest = new SqsService();
        ReflectionTestUtils.setField(classUnderTest, "sqsConfigMappingProperties", sqsMappingProperties, SqsConfigMappingProperties.class);
        ReflectionTestUtils.setField(classUnderTest, "queueMessagingTemplate", queueMessagingTemplate, QueueMessagingTemplate.class);
    }
    @AfterEach
    public void tearDown() throws Exception {
        if(sqsRestServer != null)
            sqsRestServer.stopAndWait();
    }
    /*@Test
    public void givenValidPriceChange_whenSendSqsMsg_theVerifyReceivedMsg() throws Exception {
        classUnderTest.sendSqsMessage(buildPricingChange());
        PricingChange actualResponse = queueMessagingTemplate.receiveAndConvert(QUEUE_NAME,PricingChange.class);
        assertEquals(EXPECTED_SELLING_PRICE, actualResponse.getSellingPrice().getAmount());
    }*/
    public AwsMappedProperties buildAwsConfig() {
        AwsMappedProperties awsConfig = new AwsMappedProperties();
        awsConfig.setSqsQueueName(QUEUE_NAME);
        return awsConfig;
    }
}