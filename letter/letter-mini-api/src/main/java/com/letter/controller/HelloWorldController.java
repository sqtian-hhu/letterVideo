package com.letter.controller;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "hello",tags = {"hello测试的controller"})

@RestController
//可知 @RestController注解相当于@ResponseBody 和 @Controller合在一起的作用
public class HelloWorldController {
	//http://localhost:8080/hello
	@RequestMapping("/hello")
	public String Hello() {
		return "Hello Spring Boot~";
	}
	
}
