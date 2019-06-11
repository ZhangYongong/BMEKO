package com.baizhang.bmeko.user;

import org.springframework.boot.SpringApplication;
        import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan(basePackages = "com.baizhang.bmeko.user.mapper")
@ComponentScan(basePackages = {"com.baizhang.bmeko.util"})
public class BmekoUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(BmekoUserApplication.class, args);
    }

}