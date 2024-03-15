package com.oscar.dummydiscovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class DummyDiscoveryApplication {

    public static void main(String[] args) {
        SpringApplication.run(DummyDiscoveryApplication.class, args);
    }

}
