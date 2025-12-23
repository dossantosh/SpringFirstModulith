package com.dossantosh.springfirstmodulith;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.modulith.Modulithic;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Modulithic
@SpringBootApplication
public class SpringfirstmodulithApplication {

	public static void main(String[] args) {
		
		//  BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		//  String pass = "Sb202582";
		//  String encodepass = "";
		//  encodepass = passwordEncoder.encode(pass);
		//  System.out.println(encodepass);
		
		SpringApplication.run(SpringfirstmodulithApplication.class, args);
	}

}
