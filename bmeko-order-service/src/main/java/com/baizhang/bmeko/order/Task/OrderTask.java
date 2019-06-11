package com.baizhang.bmeko.order.Task;

import com.baizhang.bmeko.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author YONGHONG ZHANG
 * @date 2019-06-01-17:09
 */
@Component
@EnableScheduling
public class OrderTask {


    @Autowired
    OrderService orderService;


    @Scheduled(cron = "0/5 * * * * ?")
    public void work() throws InterruptedException {



        System.out.println("定时检查过期订单，删除过期订单,由orderService来执行");
    }

}
