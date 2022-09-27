package com.lwx.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

@SpringBootApplication()
@ComponentScan("com.lwx")//可以支持多个包加{"com.lwx","com.lwx","com.lwx"}然后逗号分隔
public class Wiki2Application {
    private static final Logger LOG = LoggerFactory.getLogger(Wiki2Application.class);
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Wiki2Application.class);
        Environment env = app.run(args).getEnvironment();
        LOG.info("启动成功！！");
        LOG.info("地址:\thttp://127.0.0.1:{}",env.getProperty("server.port"));
    }
}
