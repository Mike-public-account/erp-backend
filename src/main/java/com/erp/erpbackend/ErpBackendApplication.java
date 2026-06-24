package com.erp.erpbackend;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ErpBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ErpBackendApplication.class, args);
	}

}
