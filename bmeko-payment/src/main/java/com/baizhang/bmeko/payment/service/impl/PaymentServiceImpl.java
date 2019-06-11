package com.baizhang.bmeko.payment.service.impl;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.baizhang.bmeko.bean.PaymentInfo;
import com.baizhang.bmeko.payment.mapper.PaymentInfoMapper;
import com.baizhang.bmeko.payment.service.PaymentService;
import com.baizhang.bmeko.util.ActiveMQUtil;
import org.apache.activemq.ScheduledMessage;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.jms.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author YONGHONG ZHANG
 * @date 2019-06-02-15:35
 */
@Service
public class PaymentServiceImpl implements PaymentService {


    @Autowired
    PaymentInfoMapper paymentInfoMapper;


    @Autowired
    ActiveMQUtil activeMQUtil;

    @Autowired
    AlipayClient alipayClient;


    @Override
    public void savePayment(PaymentInfo paymentInfo) {


        paymentInfoMapper.insertSelective(paymentInfo);

    }

    @Override
    public void updatePayment(PaymentInfo paymentInfo) {

        Example example = new Example(PaymentInfo.class);

        example.createCriteria().andEqualTo("outTradeNo",paymentInfo.getOutTradeNo());
        paymentInfoMapper.updateByExampleSelective(paymentInfo,example);

    }

    @Override
    public void sendPaymentSuccessQueue(String tradeNo, String outTradeNo,String callbackContent) {

        //修改支付信息
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setPaymentStatus("已支付");
        paymentInfo.setCallbackTime(new Date());
        paymentInfo.setOutTradeNo(outTradeNo);
        paymentInfo.setCallbackContent(callbackContent);
        paymentInfo.setOutTradeNo(tradeNo);
        updatePayment(paymentInfo);


        try {
            Connection connect = activeMQUtil.getConnection();
            connect.start();

            //第一个值表示是否使用事务，如果选择true，第二个值相当于选择0
            Session session = connect.createSession(true, Session.SESSION_TRANSACTED);
            Queue testqueue = session.createQueue("PAYMENT_SUCCESS-QUEUE");

            MessageProducer producer = session.createProducer(testqueue);

            MapMessage mapMessage = new ActiveMQMapMessage();

            mapMessage.setString("trackingNo",tradeNo);
            mapMessage.setString("outTradeNo",outTradeNo);

            producer.setDeliveryMode(DeliveryMode.PERSISTENT);
            producer.send(mapMessage);
            session.commit();
            connect.close();

        } catch (JMSException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void sendPaymentCheckQueue(String outTradeNo, int count) {

        try {
            Connection connect = activeMQUtil.getConnection();
            connect.start();

            //第一个值表示是否使用事务，如果选择true，第二个值相当于选择0
            Session session = connect.createSession(true, Session.SESSION_TRANSACTED);
            Queue testqueue = session.createQueue("PAYMENT_CHECK_QUEUE");

            MessageProducer producer = session.createProducer(testqueue);

            MapMessage mapMessage = new ActiveMQMapMessage();

            mapMessage.setInt("count",count);
            mapMessage.setString("outTradeNo",outTradeNo);
            mapMessage.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY,1000*60);


            producer.setDeliveryMode(DeliveryMode.PERSISTENT);
            producer.send(mapMessage);

            session.commit();
            connect.close();

        } catch (JMSException e) {
            e.printStackTrace();
        }


        System.out.println("发送第"+(6-count)+"次的消息队列。。。");
    }

    @Override
    public Map<String, String> checkPayment(String outTradeNo) {

        HashMap<String, String> map = new HashMap<>();


        //调用支付宝检查接口
        AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do","app_id","your private_key","json","GBK","alipay_public_key","RSA2");
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();

        HashMap<String, Object> stringObjectHashMap = new HashMap<>();

        stringObjectHashMap.put("out_trade_no",outTradeNo);

        String s = JSON.toJSONString(stringObjectHashMap);

        request.setBizContent(s);

        AlipayTradeQueryResponse response = null;
        try {
            response = alipayClient.execute(request);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        if(response.isSuccess()){

            String tradeStatus = response.getTradeStatus();
            String tradeNo = response.getTradeNo();
            String callbackContent = response.getBody();


            map.put("tradeStatus",tradeStatus);
            map.put("tradeNo",tradeNo);
            map.put("callbackContent",callbackContent);

        } else {
            System.out.println("用户未扫码");
        }

        return map;
    }

    @Override
    public boolean checkPaied(String outTradeNo) {


        boolean b = false;

        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setOutTradeNo(outTradeNo);
        PaymentInfo paymentInfo1 = paymentInfoMapper.selectOne(paymentInfo);

        if(paymentInfo1!=null&&paymentInfo1.getPaymentStatus().equals("已支付"))

            b = true;
        return false;
    }
}
