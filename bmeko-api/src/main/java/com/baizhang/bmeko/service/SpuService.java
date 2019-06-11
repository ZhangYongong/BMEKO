package com.baizhang.bmeko.service;

import com.baizhang.bmeko.bean.*;

import java.util.List;

/**
 * @author YONGHONG ZHANG
 * @date 2019-05-28-19:36
 */
public interface SpuService {
    List<SpuInfo> getSpuInfoList(String catalog3Id);

    List<BaseSaleAttr> baseSaleAttrList();

    void saveSpu(SpuInfo spuInfo);

    List<SpuSaleAttr> getSaleAttrListBySpuId(String spuId);

    List<SpuImage> getSpuImageListBySpuId(String spuId);

    List<SkuInfo> getSkuSaleAttrValueListBySpu(String spuId);
}
