package com.oscar.dummyclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class DummyClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(DummyClientApplication.class, args);
    }

}
