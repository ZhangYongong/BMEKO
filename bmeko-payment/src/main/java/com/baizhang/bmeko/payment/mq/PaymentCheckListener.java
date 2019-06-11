package com.baizhang.bmeko.payment.mq;

import com.baizhang.bmeko.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import java.util.Map;

/**
 * @author YONGHONG ZHANG
 * @date 2019-06-02-15:33
 */
@Component
public class PaymentCheckListener {

    @Autowired
    PaymentService paymentService;

    @JmsListener(containerFactory = "jmsQueueListener",destination = "PAYMENT_CHECK_QUEUE")
    public void consumePaymentSuccess(MapMessage mapMessage) throws JMSException {


        int count = mapMessage.getInt("count");
        String outTradeNo = mapMessage.getString("outTradeNo");



        //检查支付状态
        Map<String, String> map = paymentService.checkPayment(outTradeNo);
        String callbackContent = map.get("callbackContent");
        String tradeNo = map.get("tradeNo");
        String tradeStatus = map.get("tradeStatus");

        if(tradeStatus.equals("TRADE_CLOSED")||tradeStatus.equals("TRADE_SUCCESS")){

            //幂等性检查
            boolean b = paymentService.checkPaied(outTradeNo);

            if(!b){
                //发送支付成功的队列
                paymentService.sendPaymentSuccessQueue(tradeNo,outTradeNo,callbackContent);
            }



        }else{
            if(count>0){
                System.out.println("监听到延迟检查队列，执行延迟检查第"+(6-count)+"次检查");

                paymentService.sendPaymentCheckQueue(outTradeNo,count-1);
            }else{
                System.out.println("监听到延迟检查队列次数耗尽，结束检查");
            }




        }
    }
}
