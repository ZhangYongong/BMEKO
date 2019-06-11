package com.baizhang.bmeko.cart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan(basePackages ="com.baizhang.bmeko.cart.mapper")
@ComponentScan(basePackages = {"com.baizhang.bmeko.util"})
public class BmekoCartServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BmekoCartServiceApplication.class, args);
    }

}
