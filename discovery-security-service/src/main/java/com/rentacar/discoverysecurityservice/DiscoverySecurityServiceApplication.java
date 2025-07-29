package com.rentacar.discoverysecurityservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class DiscoverySecurityServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DiscoverySecurityServiceApplication.class, args);
	}

}
