package com.baizhang.bmeko.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.baizhang.bmeko.annotation.LoginRequire;
import com.baizhang.bmeko.bean.CartInfo;
import com.baizhang.bmeko.bean.SkuInfo;
import com.baizhang.bmeko.service.CartService;
import com.baizhang.bmeko.service.SkuService;
import com.baizhang.bmeko.util.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author YONGHONG ZHANG
 * @date 2019-06-01-13:53
 */
@Controller
public class CartController {

    @Reference
    SkuService skuService;

    @Reference
    CartService cartService;


    @LoginRequire(ifNeedSuccess = false)
    @RequestMapping("checkCart")
    public String checkCart(HttpServletRequest request, HttpServletResponse response, CartInfo cartInfo, ModelMap map){
        List<CartInfo> cartInfos = new ArrayList<>();
        String userId = (String) request.getAttribute("userId");

        //修改购物车的状态
        if(StringUtils.isNotBlank(userId)){
            //更新DB和缓存
            cartInfo.setUserId(userId);
            cartService.updateCartChecked(cartInfo);

        }else {
            //更新cookie
            String cartListCookieStr = CookieUtil.getCookieValue(request, "cartListCookie", true);

            if(StringUtils.isNotBlank(cartListCookieStr)){
                cartInfos = JSON.parseArray(cartListCookieStr, CartInfo.class);
                for (CartInfo info : cartInfos) {
                    if(info.getSkuId().equals(cartInfo.getSkuId())){
                        info.setIsChecked(cartInfo.getIsChecked());
                    }
                }
            }
            CookieUtil.setCookie(request,response,"cartListCookie", JSON.toJSONString(cartInfos),60*60*24*7,true);
        }

        map.put("cartList",cartInfos);

        map.put("totalPrice",getTotalPrice(cartInfos));
        return "cartListInner";
    }


    @LoginRequire(ifNeedSuccess = false)
    @RequestMapping("cartList")
    public String cartList(HttpServletRequest request, ModelMap map){
        List<CartInfo> cartInfos = new ArrayList<>();
        String userId = (String) request.getAttribute("userId");

        if(StringUtils.isBlank(userId)){
            //取cookie中数据
            String cartListCookie = CookieUtil.getCookieValue(request, "cartListCookie", true);
            if(StringUtils.isNotBlank(cartListCookie)){
                cartInfos = JSON.parseArray(cartListCookie,CartInfo.class);

            }
        }else{
            //取缓存中数据
            cartInfos = cartService.getCartCache(userId);
        }
        map.put("cartList",cartInfos);

        map.put("totalPrice",getTotalPrice(cartInfos));

        return "cartList";
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

    @LoginRequire(ifNeedSuccess = false)
    @RequestMapping("cartSuccess")
    public String cartSuccess(){

        return "success";
    }

    @LoginRequire(ifNeedSuccess = false)
    @RequestMapping("addToCart")
    public String addToCart(HttpServletRequest request, HttpServletResponse response, CartInfo cartInfo){
        String id = cartInfo.getSkuId();
        SkuInfo sku = skuService.getSkuById(id);

        cartInfo.setCartPrice(sku.getPrice().multiply(new BigDecimal(cartInfo.getSkuNum())));
        cartInfo.setIsChecked("1");
        cartInfo.setImgUrl(sku.getSkuDefaultImg());
        cartInfo.setSkuPrice(sku.getPrice());
        cartInfo.setSkuName(sku.getSkuName());


        String userId = (String) request.getAttribute("userId");
        List<CartInfo> cartInfos = new ArrayList<>();


        //用户没有登录DB，添加cookie
        if(StringUtils.isBlank(userId)){
            String cartListCookieStr = CookieUtil.getCookieValue(request, "cartListCookie", true);
            if(StringUtils.isBlank(cartListCookieStr)){

                cartInfos.add(cartInfo);
            }else {
                cartInfos = JSON.parseArray(cartListCookieStr, CartInfo.class);
                //判断是否重复的sku
                boolean b = ifnewcart(cartInfos,cartInfo);
                if(b){
                    //没有重复sku,添加
                    cartInfos.add(cartInfo);

                }else {
                    //有重复sku，更新
                    for (CartInfo info : cartInfos) {
                        String skuId = info.getSkuId();

                        if(skuId.equals(cartInfo.getSkuId())){
                            info.setSkuNum(info.getSkuNum()+cartInfo.getSkuNum());
                            info.setCartPrice(info.getSkuPrice().multiply(new BigDecimal(info.getSkuNum())));
                        }
                    }

                }

            }
            //操作完成后覆盖cookie
            CookieUtil.setCookie(request,response,"cartListCookie", JSON.toJSONString(cartInfos),60*60*24*7,true);

        }else {
            //用户已登录,添加
            String skuId = cartInfo.getSkuId();
            cartInfo.setUserId(userId);
            CartInfo cartInfoDb = cartService.ifCartExists(cartInfo);
            if(cartInfoDb!=null){
                //更新数据库
                cartInfoDb.setSkuNum(cartInfoDb.getSkuNum()+cartInfo.getSkuNum());
                cartInfoDb.setCartPrice(cartInfoDb.getSkuPrice().multiply(new BigDecimal(cartInfoDb.getSkuNum())));
                cartService.updateCart(cartInfoDb);

            }else{
                //插入数据库
                cartService.saveCart(cartInfo);
            }

            //同步缓存
            cartService.syncCache(userId);
        }

        return "redirect:/cartSuccess";
    }

    private boolean ifnewcart(List<CartInfo> cartInfos, CartInfo cartInfo) {

        boolean b = true;

        for (CartInfo info : cartInfos) {

            String skuId = info.getSkuId();

            if (skuId.equals(cartInfo.getSkuId())){
                b = false;
            }
        }


        return b;
    }

}
