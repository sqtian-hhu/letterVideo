package com.letter.controller;

import com.letter.pojo.Users;
import com.letter.service.TestService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author sqtian
 * @create 2020-08-08-14:22
 */
@Api(value = "测试查询",tags = {"测试查询的controller"})
@RestController
public class TestController {


    @Autowired
    private TestService testService;


    //http://localhost:8080/findAll
    @GetMapping("/findAll")
    public List<Users> findAll(){

        System.out.println("Controller-查询所有");
        return testService.findAll();
    }




}
