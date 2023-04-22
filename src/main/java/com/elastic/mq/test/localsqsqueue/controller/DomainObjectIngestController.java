package com.elastic.mq.test.localsqsqueue.controller;

import com.elastic.mq.test.localsqsqueue.service.SqsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DomainObjectIngestController {
    @Autowired
    SqsService sqsService;
    @PostMapping
    public ResponseEntity<?> postDomainObject(@RequestBody String message) {
        sqsService.sendSqsMessage(message);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}