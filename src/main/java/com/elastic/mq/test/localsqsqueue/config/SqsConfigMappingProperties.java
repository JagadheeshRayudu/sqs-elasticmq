package com.elastic.mq.test.localsqsqueue.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties
@Data
public class SqsConfigMappingProperties {
    private LocalServiceUri elasticMqLocalSqsUri;
    private LocalServiceUri awsSqsUri;
    private AwsMappedProperties awsConfig;
    private QueueBufferMappedProperties queueBuffer;
    private Boolean echoSqsMessagesLocal;
}