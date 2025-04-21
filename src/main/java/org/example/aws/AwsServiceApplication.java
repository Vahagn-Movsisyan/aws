package org.example.aws;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class AwsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AwsServiceApplication.class, args);
	}

}
