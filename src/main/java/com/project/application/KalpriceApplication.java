package com.project.application;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import com.project.parser.DBparser;

@SpringBootApplication
@ComponentScan(basePackages = {"com.project.application", "com.project.parser", "com.project.models"})
@MapperScan(basePackages = {"com.project.mappers"})
public class KalpriceApplication extends SpringBootServletInitializer{

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(KalpriceApplication.class);	
	}
	
	public static void main(String[] args) {
		//SpringApplication.run(KalpriceApplication.class, args);
		
		ConfigurableApplicationContext context = SpringApplication.run(KalpriceApplication.class, args);
		DBparser parser = context.getBean(DBparser.class);

		String fileSource = "C:\\Java\\smh-workspace\\kalprice\\src\\main\\resources\\static\\20231115 주간생활물가 동향 및 시세표.pdf";
		//String fileSource = "C:\\Java\\smh-workspace\\kalprice\\src\\main\\resources\\static\\230607 생활물가 시세표 및 동향.xls";
		//String fileSource = "C:\\Java\\smh-workspace\\kalprice\\src\\main\\resources\\static\\20231108 주간생활물가 동향 및 시세표.pdf";
		
		parser.parse(fileSource, "2023-11-23");
	}

}
