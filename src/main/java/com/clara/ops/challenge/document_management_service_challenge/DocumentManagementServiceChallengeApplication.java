package com.clara.ops.challenge.document_management_service_challenge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class DocumentManagementServiceChallengeApplication {

  public static void main(String[] args) {
    SpringApplication.run(DocumentManagementServiceChallengeApplication.class, args);
  }
}
