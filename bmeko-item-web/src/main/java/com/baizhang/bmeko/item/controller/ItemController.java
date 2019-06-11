package com.baizhang.bmeko.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.baizhang.bmeko.bean.*;
import com.baizhang.bmeko.service.SkuService;
import com.baizhang.bmeko.service.SpuService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author YONGHONG ZHANG
 * @date 2019-05-30-14:26
 */
@Controller
public class ItemController {

    @Reference
    SkuService skuService;

    @Reference
    SpuService spuService;


    @RequestMapping("{skuId}.html")
    public String item(@PathVariable String skuId, ModelMap map){

        SkuInfo skuInfo = skuService.getSkuById(skuId);
        map.put("skuInfo",skuInfo);
        String spuId = skuInfo.getSpuId();
        //当前sku所包含的销售属性
        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();


        List<SkuInfo> skuInfoList = spuService.getSkuSaleAttrValueListBySpu(spuId);
        Map<String, String> stringStringHashMap = new HashMap<String, String>();
        for (SkuInfo info : skuInfoList) {
            String v = info.getId();

            List<SkuSaleAttrValue> skuSaleAttrValues = info.getSkuSaleAttrValueList();
            String k ="";
            for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValues) {
                k = k + "|" + skuSaleAttrValue.getSaleAttrValueId();
            }
            stringStringHashMap.put(k,v);
        }

        String skuJson = JSON.toJSONString(stringStringHashMap);
        map.put("skuJson",skuJson);


        //spu列表
        List<SpuSaleAttr> saleAttrListBySpuId = spuService.getSaleAttrListBySpuId(spuId);

        for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {
            String saleAttrValueId = skuSaleAttrValue.getSaleAttrValueId();

            for (SpuSaleAttr spuSaleAttr : saleAttrListBySpuId) {
                List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttr.getSpuSaleAttrValueList();

                for (SpuSaleAttrValue spuSaleAttrValue : spuSaleAttrValueList) {

                    String spuSaleAttrValueId = spuSaleAttrValue.getId();

                    if (saleAttrValueId.equals(spuSaleAttrValueId)){
                        spuSaleAttrValue.setIsChecked("1");
                    }
                }
            }
        }
        map.put("spuSaleAttrListCheckBySku",saleAttrListBySpuId);


        return "item";
    }

}
