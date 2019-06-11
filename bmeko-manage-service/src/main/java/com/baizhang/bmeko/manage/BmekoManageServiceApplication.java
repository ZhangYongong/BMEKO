package com.baizhang.bmeko.manage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
//将Mapper下的bean注入到spring容器中
@MapperScan(basePackages = {"com.baizhang.bmeko.manage.mapper"})
@ComponentScan(basePackages = {"com.baizhang.bmeko.util"})
public class BmekoManageServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BmekoManageServiceApplication.class, args);
    }

}
