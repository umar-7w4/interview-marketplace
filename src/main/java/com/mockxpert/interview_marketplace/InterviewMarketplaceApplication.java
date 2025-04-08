package com.mockxpert.interview_marketplace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.mockxpert.interview_marketplace")
@PropertySource("classpath:application.properties")
@EntityScan(basePackages = "com.mockxpert.interview_marketplace.entities")
@EnableScheduling
public class InterviewMarketplaceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InterviewMarketplaceApplication.class, args);
	}

}
