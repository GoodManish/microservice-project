package com.microservice;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

@SpringBootApplication
@EnableEurekaServer
public class ServiceRegistryApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ServiceRegistryApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("-- implemented feature/branch-A feature --");
	}

	public void getLog(){
		System.out.println(" --- implemented feature/branch-B -- "+Instant.now());
	}

}
