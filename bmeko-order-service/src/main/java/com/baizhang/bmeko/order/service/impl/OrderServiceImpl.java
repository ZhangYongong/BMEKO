package com.baizhang.bmeko.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.baizhang.bmeko.bean.OrderDetail;
import com.baizhang.bmeko.bean.OrderInfo;
import com.baizhang.bmeko.order.mapper.OrderDetailMapper;
import com.baizhang.bmeko.order.mapper.OrderInfoMapper;
import com.baizhang.bmeko.service.OrderService;
import com.baizhang.bmeko.util.ActiveMQUtil;
import com.baizhang.bmeko.util.RedisUtil;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import javax.jms.*;
import java.util.List;
import java.util.UUID;

/**
 * @author YONGHONG ZHANG
 * @date 2019-06-01-17:08
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    OrderInfoMapper orderInfoMapper;

    @Autowired
    OrderDetailMapper orderDetailMapper;

    @Autowired
    ActiveMQUtil activeMQUtil;

    @Override
    public String genTradeCode(String userId) {

        String k = "user:"+userId+":tradeCode";
        String v = UUID.randomUUID().toString();

        Jedis jedis = redisUtil.getJedis();
        jedis.setex(k,60*30,v);

        jedis.close();


        return v;
    }

    @Override
    public boolean checkTradeCode(String tradeCode, String userId) {
        String k = "user:"+userId+":tradeCode";
        boolean b = false;

        Jedis jedis = redisUtil.getJedis();

        String s = jedis.get(k);
        if(StringUtils.isNotBlank(s)&&s.equals(tradeCode)){
            b = true;
            jedis.del(k);
        }


        return b;
    }

    @Override
    public String saveOrder(OrderInfo orderInfo) {
        orderInfoMapper.insertSelective(orderInfo);
        String orderId = orderInfo.getId();

        List<OrderDetail> orderDetailList = orderInfo.getOrderDetailList();
        for (OrderDetail orderDetail : orderDetailList) {
            orderDetail.setOrderId(orderId);
            orderDetailMapper.insertSelective(orderDetail);
        }
        return orderInfo.getId();
    }

    @Override
    public OrderInfo getOrderById(String orderId) {

        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setId(orderId);
        OrderInfo order = orderInfoMapper.selectByPrimaryKey(orderInfo);


        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderId(orderId);
        List<OrderDetail> orderDetails = orderDetailMapper.select(orderDetail);

        order.setOrderDetailList(orderDetails);
        return order;
    }

    @Override
    public void updateOrderStatus(OrderInfo orderInfo) {

        Example example = new Example(OrderInfo.class);
        example.createCriteria().andEqualTo("outTradeNo",orderInfo.getOutTradeNo());
        orderInfoMapper.updateByExampleSelective(orderInfo,example) ;
    }

    @Override
    public void sendOrderResultQueue(String outTradeNo) {
        try {
            Connection connect = activeMQUtil.getConnection();
            connect.start();

            //第一个值表示是否使用事务，如果选择true，第二个值相当于选择0
            Session session = connect.createSession(true, Session.SESSION_TRANSACTED);
            Queue testqueue = session.createQueue("ORDER_RESULT_QUEUE");

            MessageProducer producer = session.createProducer(testqueue);

            TextMessage textMessage = new ActiveMQTextMessage();

            // 获得订单的消息数据
            OrderInfo orderInfo = new OrderInfo();
            orderInfo.setOutTradeNo(outTradeNo);
            orderInfo = orderInfoMapper.selectOne(orderInfo);

            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderInfo.getId());
            List<OrderDetail> orderDetails = orderDetailMapper.select(orderDetail);
            orderInfo.setOrderDetailList(orderDetails);

            //将消息数据转化为json字符串文本输出
            textMessage.setText(JSON.toJSONString(orderInfo));

            producer.setDeliveryMode(DeliveryMode.PERSISTENT);
            producer.send(textMessage);
            session.commit();
            connect.close();

        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}
