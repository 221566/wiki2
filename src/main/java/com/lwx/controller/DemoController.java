package com.lwx.controller;

import com.lwx.domain.Demo;
import com.lwx.service.DemoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/demo")
public class DemoController {
    @Resource
    private DemoService demoService;

    @GetMapping("/selectDemo")
    public List<Demo> selectDemo(){
        return demoService.selectDemo();
    }
}
