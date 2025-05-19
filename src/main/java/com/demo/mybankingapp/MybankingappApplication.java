package com.demo.mybankingapp;

import com.demo.mybankingapp.configuration.H2ServerConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class MybankingappApplication {

	public static void main(String[] args) {
		SpringApplication.run(MybankingappApplication.class, args);
	}
	//test push
}
