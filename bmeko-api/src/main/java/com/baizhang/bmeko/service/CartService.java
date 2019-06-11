package com.baizhang.bmeko.service;

import com.baizhang.bmeko.bean.CartInfo;

import java.util.List;

/**
 * @author YONGHONG ZHANG
 * @date 2019-06-01-12:02
 */
public interface CartService {
    CartInfo ifCartExists(CartInfo cartInfo);

    void updateCart(CartInfo cartInfoDb);

    void saveCart(CartInfo cartInfo);

    void syncCache(String userId);

    List<CartInfo> getCartCache(String userId);

    void updateCartChecked(CartInfo cartInfo);

    void combineCart(List<CartInfo> cartInfos, String id);

    List<CartInfo> getCartCacheByChecked(String userId);

    void deleteCartbyId(List<CartInfo> cartInfos);
}
