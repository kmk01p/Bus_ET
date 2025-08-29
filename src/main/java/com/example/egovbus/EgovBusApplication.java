package com.example.egovbus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;

/**
 * Ethiopia Real-time Bus Monitoring Platform
 * Main Application with eGovFrame Configuration
 * 
 * @author Ethiopia Bus Monitoring System
 * @version 1.0
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.example.egovbus", "egovframework"})
@EnableJpaRepositories(basePackages = "com.example.egovbus.repository")
@EntityScan(basePackages = "com.example.egovbus.model")
@EnableScheduling
public class EgovBusApplication {

    public static void main(String[] args) {
        SpringApplication.run(EgovBusApplication.class, args);
        System.out.println("================================================");
        System.out.println("  Ethiopia Bus Monitoring System Started!");
        System.out.println("  Access: http://localhost:8080");
        System.out.println("================================================");
    }
}