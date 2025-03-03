package com.whimsy.wish;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@ComponentScan(basePackages = {
		"com.whimsy.wish",
		"com.whimsy.wish.controller",
		"com.whimsy.wish.service",
		"com.whimsy.wish.repository",
		"com.whimsy.wish.config"
})
public class WhimsyWishBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(WhimsyWishBackendApplication.class, args);
	}

}
