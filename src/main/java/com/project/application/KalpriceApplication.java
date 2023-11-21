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
		//String fileSource = "C:\\Java\\smh-workspace\\kalprice\\src\\main\\resources\\static\\20231115 주간생활물가 동향 및 시세표.pdf";
		String fileSource = "C:\\Java\\smh-workspace\\kalprice\\src\\main\\resources\\static\\230607 생활물가 시세표 및 동향.xls";

		parser.parse(fileSource);
	}

}
