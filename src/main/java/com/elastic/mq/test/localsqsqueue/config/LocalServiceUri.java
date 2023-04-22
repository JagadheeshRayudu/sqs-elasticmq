package com.elastic.mq.test.localsqsqueue.config;

import lombok.Data;

@Data
public class LocalServiceUri {
    private String scheme;
    private String host;
    private String path;
    private String port;
}