package com.example.minitwitter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@EnableJpaAuditing
@SpringBootApplication
public class MiniTwitterApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(MiniTwitterApiApplication.class, args);
	}

}
