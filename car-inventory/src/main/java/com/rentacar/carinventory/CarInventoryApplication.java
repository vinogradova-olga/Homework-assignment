package com.rentacar.carinventory;

import org.mapstruct.MapperConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;


import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication
public class CarInventoryApplication{

	public static void main(String[] args) {
		SpringApplication.run(CarInventoryApplication.class, args);
	}
}
