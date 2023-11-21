package com.project.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.project.parser.DBparser;

@SpringBootApplication
@ComponentScan(basePackages = {"com.project.parser"})
public class KalpriceApplication {

	public static void main(String[] args) {
		SpringApplication.run(KalpriceApplication.class, args);
		DBparser parser = new DBparser();
		parser.parse();
	}

}
