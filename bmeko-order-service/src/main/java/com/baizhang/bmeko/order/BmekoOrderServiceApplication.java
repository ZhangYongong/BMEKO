package com.baizhang.bmeko.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan(basePackages ="com.baizhang.bmeko.order.mapper")
@ComponentScan(basePackages = {"com.baizhang.bmeko.util"})
public class BmekoOrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BmekoOrderServiceApplication.class, args);
    }

}
