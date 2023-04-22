package com.elastic.mq.test.localsqsqueue.service;

import com.elastic.mq.test.localsqsqueue.config.SqsConfigMappingProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@EnableConfigurationProperties(SqsConfigMappingProperties.class)
public class SqsService {
    @Autowired
    SqsConfigMappingProperties sqsConfigMappingProperties;
    @Autowired
    QueueMessagingTemplate queueMessagingTemplate;
    public void sendSqsMessage(String message) {
        queueMessagingTemplate.convertAndSend(sqsConfigMappingProperties.getAwsConfig().getSqsQueueName(), MessageBuilder.withPayload(message).build());
    }
    @SqsListener(value = "jagadheesh_queue")
    public void queueListener(String message){
        log.info("Message received from ElasticMQ ==>  {}",message);
    }
}
