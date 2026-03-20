package com.dossantosh.springfirstmodulith;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.modulith.Modulithic;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@Modulithic
@EnableMethodSecurity
@SpringBootApplication
public class SpringfirstmodulithApplication {

	static void main(String[] args) {

		SpringApplication.run(SpringfirstmodulithApplication.class, args);
	}

}
