package com.intelliops;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@EnableCaching
@EnableScheduling
public class IntelliOpsApplication {

    public static void main(String[] args) {
        SpringApplication.run(IntelliOpsApplication.class, args);
    }
}
