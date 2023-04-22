package com.elastic.mq.test.localsqsqueue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LocalSqsQueueApplication {

	public static void main(String[] args) {
		System.setProperty("spring.profiles.active","local");
		SpringApplication.run(LocalSqsQueueApplication.class, args);
	}

}
