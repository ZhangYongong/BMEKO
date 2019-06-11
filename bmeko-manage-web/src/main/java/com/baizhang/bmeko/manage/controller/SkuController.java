package com.baizhang.bmeko.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baizhang.bmeko.bean.SkuInfo;
import com.baizhang.bmeko.service.SkuService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author YONGHONG ZHANG
 * @date 2019-05-29-15:18
 */
@Controller
public class SkuController {

    @Reference
    private SkuService skuService;

    @ResponseBody
    @RequestMapping("saveSku")
    public String saveSku(SkuInfo skuInfo){
        skuService.saveSku(skuInfo);
        return "success";
    }


    @ResponseBody
    @RequestMapping("getSkuListBySpu")
    public List<SkuInfo> getSkuListBySpu(String spuId){
        List<SkuInfo> skuInfoList = skuService.getSkuListBySpu(spuId);
        return skuInfoList;
    }
}

