package com.elastic.mq.test.localsqsqueue.config;

import lombok.Data;

@Data
public class AwsMappedProperties {
    private String accessKey;
    private String secretKey;
    private String sqsQueueName;
}