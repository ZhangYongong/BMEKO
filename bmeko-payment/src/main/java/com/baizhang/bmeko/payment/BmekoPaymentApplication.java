package com.baizhang.bmeko.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan(basePackages ="com.baizhang.bmeko.payment.mapper")
@ComponentScan(basePackages = {"com.baizhang.bmeko.util"})
public class BmekoPaymentApplication {

    public static void main(String[] args) {
        SpringApplication.run(BmekoPaymentApplication.class, args);
    }

}
