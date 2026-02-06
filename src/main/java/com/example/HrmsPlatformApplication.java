package com.example;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Unified HRMS application entry point.
// All modules live under the base package: com.example.hrms_platform
@SpringBootApplication
public class HrmsPlatformApplication {

	public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();

        dotenv.entries().forEach(e ->
                System.setProperty(e.getKey(), e.getValue())
        );

        SpringApplication.run(HrmsPlatformApplication.class, args);

	}

}
