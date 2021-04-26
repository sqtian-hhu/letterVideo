package com.letter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
//设置要扫描的mapper(dao)
@MapperScan(basePackages="com.letter.dao")
//设置要扫描的Component
@ComponentScan(basePackages= {"com.letter","org.n3r.idworker"})
public class Application {
	
	public static void main(String[] args) {
		SpringApplication.run(com.letter.Application.class, args);
	}
	
}
