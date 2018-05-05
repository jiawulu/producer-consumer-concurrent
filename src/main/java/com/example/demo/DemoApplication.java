package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}


	@Bean
	public ThreadPoolTaskExecutor myThreadPoolTaskExecutor(){

		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setThreadGroupName("demo");
		executor.setThreadNamePrefix("demo-migrate");

		executor.setCorePoolSize(10);
		executor.setMaxPoolSize(100);
		executor.setQueueCapacity(1000);

		return executor;

	}

}
