package com.example.takehome;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
/**
 * Main application class.
 */
@Slf4j
@EnableCaching
@SpringBootApplication
public class TakehomeApplication {
	public static void main(String[] args) {
		SpringApplication.run(TakehomeApplication.class, args);
	}
}
