package com.oscar.dummyserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class DummyServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DummyServerApplication.class, args);
    }

}
