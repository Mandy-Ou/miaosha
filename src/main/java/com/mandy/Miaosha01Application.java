package com.mandy;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan(value = "com.mandy.dao")
@SpringBootApplication
public class Miaosha01Application {

    public static void main(String[] args) {
        SpringApplication.run(Miaosha01Application.class, args);
    }

}
