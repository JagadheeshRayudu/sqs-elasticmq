package com.elastic.mq.test.localsqsqueue.service;

import com.elastic.mq.test.localsqsqueue.config.SqsConfigMappingProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@EnableConfigurationProperties(SqsConfigMappingProperties.class)
@RequiredArgsConstructor
public class SqsService {
    private final SqsConfigMappingProperties sqsConfigMappingProperties;
    private final QueueMessagingTemplate queueMessagingTemplate;
    public void sendSqsMessage(String message) {
        queueMessagingTemplate.convertAndSend(sqsConfigMappingProperties.getAwsConfig().getSqsQueueName(), MessageBuilder.withPayload(message).build());
    }
    @SqsListener(value = "sqs-queue-name")
    public void queueListener(String message){
        log.info("Message received from ElasticMQ ==>  {}",message);
    }
}
