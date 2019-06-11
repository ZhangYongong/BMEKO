package com.baizhang.bmeko.interceptor;

import com.baizhang.bmeko.annotation.LoginRequire;
import com.baizhang.bmeko.util.CookieUtil;
import com.baizhang.bmeko.util.HttpClientUtil;
import com.baizhang.bmeko.util.JwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author YONGHONG ZHANG
 * @date 2019-06-01-14:26
 */
@Component
public class AuthInterceptor extends HandlerInterceptorAdapter {

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //判断访问的方法是否需要拦截
        HandlerMethod method = (HandlerMethod) handler;

        LoginRequire methodAnnotation = method.getMethodAnnotation(LoginRequire.class);

        if (methodAnnotation == null) {

            return true;
        }

        String oldToken = CookieUtil.getCookieValue(request, "oldToken", true);
        String newToken = request.getParameter("newToken");
        String token = "";

        if(StringUtils.isNotBlank(oldToken)&&StringUtils.isBlank(newToken)){
            //登陆过
            token = oldToken;
        }

        if(StringUtils.isBlank(oldToken)&&StringUtils.isNotBlank(newToken)){
            //第一次登陆
            token = newToken;
        }

        if(StringUtils.isBlank(oldToken)&&StringUtils.isBlank(newToken)){
            //从未登陆
        }

        if(StringUtils.isNotBlank(oldToken)&&StringUtils.isNotBlank(newToken)){
            //登陆过期
            token = newToken;
        }


        if (methodAnnotation.ifNeedSuccess() && StringUtils.isBlank(token)) {

            StringBuffer requestURL = request.getRequestURL();
            response.sendRedirect("http://passport.bmeko.com:8085/index?returnURL="+requestURL);
            return false;
        }


        String success = "";
        if (StringUtils.isNotBlank(token)) {
            //远程访问passport，验证token

            success = HttpClientUtil.doGet("http://passport.bmeko.com:8085/verify?token="+token+"&salt="+getCurrentIp(request));
        }

        if (!success.equals("success") && methodAnnotation.ifNeedSuccess()) {


            response.sendRedirect("http://passport.bmeko.com:8085/index");
            return false;

        }

        if (!success.equals("success") && !methodAnnotation.ifNeedSuccess()) {

            //购物车方法

            return true;
        }

        if(success.equals("success")){
            //cookie验证通过，重新刷新cookie的过期时间
            CookieUtil.setCookie(request,response,"oldToken",token,60*60*2,true);

            //将用户信息放入应用请求
            Map userMap = JwtUtil.decode("baizhang", token, getCurrentIp(request));
            request.setAttribute("userId",userMap.get("userId"));
            request.setAttribute("nickName",userMap.get("nickName"));
        }




        return true;
    }


    private String getCurrentIp(HttpServletRequest request) {
        String ip = request.getRemoteAddr();//直接获取ip

        //使用负载均衡获取IP方式
//            request.getHeader("x-forwarded-for");

        if(StringUtils.isBlank(ip)){
            ip = "127.0.0.1";//设置一个虚拟ip
        }
        return ip;
    }
}
