package com.baizhang.bmeko.payment.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.baizhang.bmeko.bean.OrderInfo;
import com.baizhang.bmeko.bean.PaymentInfo;
import com.baizhang.bmeko.payment.service.PaymentService;
import com.baizhang.bmeko.payment.util.AlipayConfig;
import com.baizhang.bmeko.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author YONGHONG ZHANG
 * @date 2019-06-02-15:30
 */
@Controller
public class PaymentController {

    @Reference
    OrderService orderService;

    @Autowired
    AlipayClient alipayClient;

    @Autowired
    PaymentService paymentService;


    @RequestMapping("alipay/callback/return")
    public String callbackReturn(HttpServletRequest request, String orderId, ModelMap map){


        Map<String, String> paramsMap =null; //将异步通知中收到的所有参数都存放到map中

        boolean signVerified =false;
        try {
            signVerified = AlipaySignature.rsaCheckV1(paramsMap, AlipayConfig.alipay_public_key, AlipayConfig.charset, AlipayConfig.sign_type); //调用SDK验证签名
        } catch (Exception e) {
            System.out.println("此处支付宝的签名验证通过。。。");
        }
        if(signVerified){
            // TODO 验签成功后，按照支付结果异步通知中的描述，对支付结果中的业务内容进行二次校验，校验成功后在response中返回success并继续商户自身业务处理，校验失败返回failure
            String tradeNo = request.getParameter("trade_no");
            String outTradeNo = request.getParameter("out_trade_no");

            String callbackContent = request.getQueryString();

            //幂等性检查
            boolean b = paymentService.checkPaied(outTradeNo);
            if(!b){
                //发送支付成功的消息PAYMENT_SUCCESS-QUEUE
                paymentService.sendPaymentSuccessQueue(tradeNo,outTradeNo,callbackContent);
            }



        }else{
            // TODO 验签失败则记录异常日志，并在response中返回failure.
            //返回失败的页面
        }






        return "testPaySuccess";
    }



    @RequestMapping("alipay/submit")
    @ResponseBody
    public String alipay( String orderId, ModelMap map){


        String userid = "2";

        OrderInfo order = orderService.getOrderById(orderId);

        //生成和保存支付信息
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setOutTradeNo(order.getOutTradeNo());
        paymentInfo.setPaymentStatus("未支付");
        paymentInfo.setOrderId(order.getId());
        paymentInfo.setTotalAmount(order.getTotalAmount());
        paymentInfo.setSubject(order.getOrderDetailList().get(0).getSkuName());
        paymentInfo.setCreateTime(new Date());


        paymentService.savePayment(paymentInfo);


        //重定向到支付宝平台
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();//创建API对应的request
        alipayRequest.setReturnUrl(AlipayConfig.return_payment_url);
        alipayRequest.setNotifyUrl(AlipayConfig.notify_payment_url);//在公共参数中设置回跳和通知地址

        HashMap<String, Object> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put("out_trade_no",order.getOutTradeNo());
        stringStringHashMap.put("product_code","FAST_INSTANT_TRADE_PAY");
        stringStringHashMap.put("total_amount",order.getTotalAmount());
        stringStringHashMap.put("subject","测试硅谷手机phoneX");

        String json = JSON.toJSONString(stringStringHashMap);
        alipayRequest.setBizContent(json);

        String form="";
        try {
            form = alipayClient.pageExecute(alipayRequest).getBody(); //调用SDK生成表单
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        System.out.println("设置一个定时巡检订单"+paymentInfo.getOutTradeNo()+"的支付状态的延迟队列");


        paymentService.sendPaymentCheckQueue(paymentInfo.getOutTradeNo(),5);



        return form;
    }

    @RequestMapping("mx/submit")
    public String mx(String orderId, ModelMap map){

        //重定向到财付通平台
        return "index";
    }



    @RequestMapping("index")
    public String index(String orderId, ModelMap map){

        String userId= "2";

        OrderInfo orderInfo = orderService.getOrderById(orderId);

        map.put("totalAmount",orderInfo.getTotalAmount());
        map.put("orderId",orderId);
        map.put("outTradeNo",orderInfo.getOutTradeNo());

        return "index";
    }
}
