package com.baizhang.bmeko.payment.service;

import com.baizhang.bmeko.bean.PaymentInfo;

import java.util.Map;

/**
 * @author YONGHONG ZHANG
 * @date 2019-06-02-15:34
 */
public interface PaymentService {
    void savePayment(PaymentInfo paymentInfo);

    void updatePayment(PaymentInfo paymentInfo);

    void sendPaymentSuccessQueue(String tradeNo, String outTradeNo,String callbackContent);

    void sendPaymentCheckQueue(String outTradeNo, int i);

    Map<String, String> checkPayment(String outTradeNo);

    boolean checkPaied(String outTradeNo);
}
