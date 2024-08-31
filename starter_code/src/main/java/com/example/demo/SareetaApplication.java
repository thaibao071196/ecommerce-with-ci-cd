package com.example.demo;

import org.apache.logging.log4j.*;
import org.apache.logging.log4j.core.LoggerContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@EnableJpaRepositories("com.example.demo.model.persistence.repositories")
@EntityScan("com.example.demo.model.persistence")
public class SareetaApplication {
    private static Logger log = ((LoggerContext) LogManager.getContext(false)).getLogger(SareetaApplication.class.getName());
    public static void main(String[] args) {
        log.info("Application run success at port: 8080");
        SpringApplication.run(SareetaApplication.class, args);
    }
}
