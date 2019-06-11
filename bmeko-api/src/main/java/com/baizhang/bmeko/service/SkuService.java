package com.baizhang.bmeko.service;

import com.baizhang.bmeko.bean.SkuInfo;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author YONGHONG ZHANG
 * @date 2019-05-28-19:36
 */
public interface SkuService {
    List<SkuInfo> getSkuListBySpu(String spuId);

    void saveSku(SkuInfo skuInfo);

    SkuInfo getSkuById(String skuId);

    List<SkuInfo> getSkuLIstByCatalog3Id(String catalog3Id);

    boolean checkPrice(BigDecimal skuPrice, String skuId);
}
