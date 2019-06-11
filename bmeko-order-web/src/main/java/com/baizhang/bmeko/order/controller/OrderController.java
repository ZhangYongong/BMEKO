package com.baizhang.bmeko.order.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baizhang.bmeko.annotation.LoginRequire;
import com.baizhang.bmeko.bean.CartInfo;
import com.baizhang.bmeko.bean.OrderDetail;
import com.baizhang.bmeko.bean.OrderInfo;
import com.baizhang.bmeko.bean.UserAddress;
import com.baizhang.bmeko.bean.enums.PaymentWay;
import com.baizhang.bmeko.service.CartService;
import com.baizhang.bmeko.service.OrderService;
import com.baizhang.bmeko.service.SkuService;
import com.baizhang.bmeko.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author YONGHONG ZHANG
 * @date 2019-06-01-16:51
 */
@Controller
public class OrderController {

    @Reference
    CartService cartService;

    @Reference
    UserService userService;

    @Reference
    OrderService orderService;

    @Reference
    SkuService skuService;


    @LoginRequire(ifNeedSuccess = true)
    @RequestMapping("submitOrder")
    public String submitOrder(String tradeCode, HttpServletRequest request, HttpServletResponse response, ModelMap map){
        String userId = (String) request.getAttribute("userId");

        //比较交易码
        boolean btradeCode = orderService.checkTradeCode(tradeCode,userId);

        //订单对象
        OrderInfo orderInfo = new OrderInfo();
        List<OrderDetail> orderDetails = new ArrayList<>();

        //提交订单业务
        if(btradeCode){

            //获取购物车中被选中的商品数据
            List<CartInfo> cartInfos = cartService.getCartCacheByChecked(userId);

            //生成订单信息
            //验价，验库存
            for (CartInfo cartInfo : cartInfos) {
                OrderDetail orderDetail = new OrderDetail();
                BigDecimal skuPrice = cartInfo.getSkuPrice();
                String skuId = cartInfo.getSkuId();
                //验价
                boolean bprice = skuService.checkPrice(skuPrice,skuId);
                //验库存

                if(bprice){
                    //封装商品信息
                    orderDetail.setSkuName(cartInfo.getSkuName());
                    orderDetail.setSkuId(cartInfo.getSkuId());
                    orderDetail.setOrderPrice(cartInfo.getCartPrice());
                    orderDetail.setImgUrl(cartInfo.getImgUrl());
                    orderDetail.setSkuNum(cartInfo.getSkuNum());

                    orderDetails.add(orderDetail);

                }else {
                    //sku校验失败
                    map.put("errMsg","订单中的商品价格（库存）发生变化，请重新确认订单");
                    return "tradeFail";
                }
            }

            orderInfo.setOrderDetailList(orderDetails);
            //封装订单信息
            orderInfo.setProcessStatus("订单未支付");
            //日期
            //日期计算
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE,1);
            orderInfo.setExpireTime(c.getTime());
            orderInfo.setOrderStatus("未支付");
            String consignee = "测试收件人";
            orderInfo.setConsignee(consignee);
            //外部订单号
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String format = sdf.format(new Date());

            String outTradeNo = "BAIZHANG"+format+System.currentTimeMillis();
            orderInfo.setOutTradeNo(outTradeNo);
            orderInfo.setPaymentWay(PaymentWay.ONLINE);
            orderInfo.setUserId(userId);
            orderInfo.setTotalAmount(getTotalPrice(cartInfos));
            orderInfo.setOrderComment("博铭订单");
            String address = "测试收件地址";
            orderInfo.setDeliveryAddress(address);
            orderInfo.setCreateTime(new Date());
            String tel = "122345654567";
            orderInfo.setConsigneeTel(tel);

            String orderId = orderService.saveOrder(orderInfo);

            //删除购物车中的提交的商品信息，同步缓存
            cartService.deleteCartbyId(cartInfos);


            //对接支付系统接口

            return "redirect:http://payment.bmeko.com:8087/index?orderId="+orderId;
        }else {
            map.put("errMsg","获取订单信息失败");
            return "tradeFail";
        }


    }



    @LoginRequire(ifNeedSuccess = true)
    @RequestMapping("toTrade")
    public String toTrade(HttpServletRequest request, HttpServletResponse response, CartInfo cartInfo, ModelMap map){
        //需要被单点登录的拦截器
        String userId = (String) request.getAttribute("userId");

        //将被选中的购物车对象转化为订单对象，展示出来
        List<CartInfo> cartInfos = cartService.getCartCacheByChecked(userId);


        List<OrderDetail> orderDetails = new ArrayList<>();
        for (CartInfo info : cartInfos) {
            OrderDetail orderDetail = new OrderDetail();

            //将购物车对象转化成订单对象
            orderDetail.setImgUrl(info.getImgUrl());
            orderDetail.setSkuId(info.getSkuId());
            orderDetail.setOrderPrice(info.getCartPrice());
            orderDetail.setSkuName(info.getSkuName());
            orderDetails.add(orderDetail);
        }
        //查询用户收货地址列表，让用户选择
        List<UserAddress> userAddressList = userService.getUserAddressList(userId);


        //生成交易码
        String tradeCode = orderService.genTradeCode(userId);

        map.put("tradeCode",tradeCode);
        map.put("userAddressList",userAddressList);
        map.put("orderDetailList",orderDetails);
        map.put("totalAmount",getTotalPrice(cartInfos));

        return "trade";
    }


    private BigDecimal getTotalPrice(List<CartInfo> cartInfos) {

        BigDecimal b = new BigDecimal("0");

        for (CartInfo cartInfo : cartInfos) {

            if(cartInfo.getIsChecked().equals("1")){
                b = b.add(cartInfo.getCartPrice());

            }

        }
        return b;
    }
}
