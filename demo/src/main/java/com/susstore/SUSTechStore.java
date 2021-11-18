package com.susstore;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

@SpringBootApplication
@MapperScan("com.susstore.mapper")
public class SUSTechStore {

    public static void main(String[] args) {
        SpringApplication.run(SUSTechStore.class, args);
    }

}
