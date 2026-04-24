package com.dossantosh.springfirstmodulith;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.modulith.Modulithic;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Modulithic
@EnableMethodSecurity
@SpringBootApplication
public class SpringfirstmodulithApplication {

	private static final String HASH_PASSWORD_ARG = "--hash-password=";

	public static void main(String[] args) {

		for (String arg : args) {
			if (arg != null && arg.startsWith(HASH_PASSWORD_ARG)) {
				String rawPassword = arg.substring(HASH_PASSWORD_ARG.length());
				System.out.println(new BCryptPasswordEncoder().encode(rawPassword));
				return;
			}
		}

		SpringApplication.run(SpringfirstmodulithApplication.class, args);
	}

}
