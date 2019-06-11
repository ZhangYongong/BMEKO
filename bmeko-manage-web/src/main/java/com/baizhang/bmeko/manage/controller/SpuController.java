package com.baizhang.bmeko.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baizhang.bmeko.bean.BaseSaleAttr;
import com.baizhang.bmeko.bean.SpuImage;
import com.baizhang.bmeko.bean.SpuInfo;
import com.baizhang.bmeko.bean.SpuSaleAttr;
import com.baizhang.bmeko.manage.util.MyUploadUtil;
import com.baizhang.bmeko.service.SpuService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author YONGHONG ZHANG
 * @date 2019-05-29-15:19
 */
@Controller
public class SpuController {

    @Reference
    private SpuService spuService;

    @ResponseBody
    @RequestMapping("getSpuImageListBySpuId")
    public List<SpuImage> getSpuImageListBySpuId(String spuId){
        List<SpuImage> spuImages = spuService.getSpuImageListBySpuId(spuId);
        return spuImages;
    }

    @ResponseBody
    @RequestMapping("getSaleAttrListBySpuId")
    public List<SpuSaleAttr> getSaleAttrListBySpuId(String spuId){
        List<SpuSaleAttr> spuSaleAttrs = spuService.getSaleAttrListBySpuId(spuId);

        return spuSaleAttrs;
    }


    @ResponseBody
    @RequestMapping("fileUpload")
    public String fileUpload(@RequestParam("file") MultipartFile file){
        String url = MyUploadUtil.uploadImage(file);
        System.out.println(url);
        return url;
    }

    @ResponseBody
    @RequestMapping("spuList")
    public List<SpuInfo> spuList(String catalog3Id){

        List<SpuInfo> SpuInfos = spuService.getSpuInfoList(catalog3Id);

        return SpuInfos;
    }


    @ResponseBody
    @RequestMapping("saveSpu")
    public String saveSpu(SpuInfo spuInfo){

        spuService.saveSpu(spuInfo);

        return "success";
    }

    @ResponseBody
    @RequestMapping("getSpuInfoList")
    public List<SpuInfo> getSpuInfoList(String catalog3Id){
        List<SpuInfo> SpuInfos = spuService.getSpuInfoList(catalog3Id);
        return SpuInfos;
    }

    @ResponseBody
    @RequestMapping("baseSaleAttrList")
    public List<BaseSaleAttr> baseSaleAttrList(){
        List<BaseSaleAttr> baseSaleAttrs = spuService.baseSaleAttrList();
        return baseSaleAttrs;
    }


}
