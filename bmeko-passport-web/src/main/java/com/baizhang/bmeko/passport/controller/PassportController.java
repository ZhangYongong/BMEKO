package com.baizhang.bmeko.passport.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.baizhang.bmeko.bean.CartInfo;
import com.baizhang.bmeko.bean.UserInfo;
import com.baizhang.bmeko.service.CartService;
import com.baizhang.bmeko.service.UserService;
import com.baizhang.bmeko.util.CookieUtil;
import com.baizhang.bmeko.util.JwtUtil;
import io.jsonwebtoken.SignatureException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author YONGHONG ZHANG
 * @date 2019-06-01-16:41
 */
@Controller
public class PassportController {

    @Reference
    UserService userService;

    @Reference
    CartService cartService;

    /**
     * 登录页
     * @return
     */
    @RequestMapping("index")
    public String index(String returnURL, ModelMap map){

        map.put("returnURL",returnURL);
        return "index";
    }

    //颁发token
    @RequestMapping("login")
    @ResponseBody
    public String login(HttpServletRequest request, HttpServletResponse response, UserInfo userInfo){

        //调用用户服务验证用户名和密码
        UserInfo user = userService.login(userInfo);

        if(user==null){
            //用户名或密码错误
            return  "username or password err";
        }else {

            //颁发token,使用jwt，重定向原始业务
            HashMap<String, String> stringStringHashMap = new HashMap<>();
            stringStringHashMap.put("userId",user.getId());
            stringStringHashMap.put("nickName",user.getNickName());


            String token = JwtUtil.encode("baizhang", stringStringHashMap, getMyIp(request));

            //合并购物车数据
            String cartListCookie = CookieUtil.getCookieValue(request, "cartListCookie", true);

            List<CartInfo> cartInfos = null;

            if(StringUtils.isNotBlank(cartListCookie)){
                cartInfos = JSON.parseArray(cartListCookie, CartInfo.class);
            }
            cartService.combineCart(cartInfos,user.getId());
            //删除cookie中的购物车数据
            CookieUtil.setCookie(request,response,"cartListCookie","",0,true);

            return token;


        }
    }

    private String getMyIp(HttpServletRequest request) {
        String ip = request.getRemoteAddr();//直接获取ip

        //使用负载均衡获取IP方式
//            request.getHeader("x-forwarded-for");

        if(StringUtils.isBlank(ip)){
            ip = "127.0.0.1";//设置一个虚拟ip
        }
        return ip;
    }


    //验证token
    @ResponseBody
    @RequestMapping("verify")
    public String verify(String token,String salt){

        Map<String,String> userMap = null;
        try {
            userMap = JwtUtil.decode("baizhang", token, salt);
        } catch (SignatureException e) {
            e.printStackTrace();
            return "fail";

        }

        if(userMap!=null){
            return "success";
        }else {
            return "fail";
        }
    }
}
