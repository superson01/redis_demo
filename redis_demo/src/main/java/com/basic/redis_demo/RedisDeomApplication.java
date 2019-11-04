package com.basic.redis_demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class RedisDeomApplication {
	public static void main(String[] args) {
		SpringApplication.run(RedisDeomApplication.class, args);
	}
}
