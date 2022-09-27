package com.lwx.controller;

import com.lwx.domain.Test;
import com.lwx.service.TestService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class TestController {
    @Resource
    private TestService testService;

    @GetMapping("/hello")
    public String hello(){
        return "Hello World";
    }

    @PostMapping("/hello/post")
    public String helloPost(String name){
        return "Hello World POST," + name;
    }

    @GetMapping("/test/selectTest")
    public List<Test> selectTest(){
        return testService.selectTest();
    }
}
