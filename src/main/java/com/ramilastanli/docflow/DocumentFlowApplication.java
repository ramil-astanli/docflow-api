package com.ramilastanli.docflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.integration.config.EnableIntegration;

@SpringBootApplication
@EnableIntegration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class DocumentFlowApplication {

	public static void main(String[] args) {
		SpringApplication.run(DocumentFlowApplication.class, args);
	}

}
