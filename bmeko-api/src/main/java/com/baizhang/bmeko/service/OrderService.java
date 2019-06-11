package com.baizhang.bmeko.service;

import com.baizhang.bmeko.bean.OrderInfo;

/**
 * @author YONGHONG ZHANG
 * @date 2019-06-01-12:03
 */
public interface OrderService {
    String genTradeCode(String userId);

    boolean checkTradeCode(String tradeCode, String userId);

    String saveOrder(OrderInfo orderInfo);

    OrderInfo getOrderById(String orderId);

    void updateOrderStatus(OrderInfo orderInfo);

    void sendOrderResultQueue(String outTradeNo);
}
