package com.baizhang.bmeko.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baizhang.bmeko.bean.BaseAttrInfo;
import com.baizhang.bmeko.bean.BaseAttrValue;
import com.baizhang.bmeko.service.AttrService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author YONGHONG ZHANG
 * @date 2019-05-29-15:17
 */
@Controller
public class AttrController {

    @Reference
    private AttrService attrService;


    @ResponseBody
    @RequestMapping("getAttrListByCgt3Id")
    public List<BaseAttrInfo> getAttrListByCgt3Id(String catalog3Id){
        List<BaseAttrInfo> baseAttrInfos = attrService.getAttrListByCgt3Id(catalog3Id);
        return baseAttrInfos;
    }

    @ResponseBody
    @RequestMapping("getAttrList")
    public List<BaseAttrInfo> getAttrList(String catalog3Id){
        List<BaseAttrInfo> baseAttrInfos = attrService.getAttrList(catalog3Id);
        return baseAttrInfos;
    }

    @ResponseBody
    @RequestMapping("attrList")
    public List<BaseAttrInfo> attrList(String catalog3Id){
        List<BaseAttrInfo> baseAttrInfos = attrService.getAttrList(catalog3Id);
        return baseAttrInfos;
    }

    @ResponseBody
    @RequestMapping("saveAttr")
    public String saveAttr(BaseAttrInfo baseAttrInfo){
        attrService.saveAttr(baseAttrInfo);
        return "success";
    }

    @ResponseBody
    @RequestMapping("getAttrValueList")
    public List<BaseAttrValue> getAttrValueList(String attrId){
        List<BaseAttrValue> baseAttrValues = attrService.getAttrValueList(attrId);
        return baseAttrValues;
    }

    @ResponseBody
    @RequestMapping("deleteAttrInfo")
    public String deleteAttrInfo(BaseAttrInfo baseAttrInfo){
        attrService.deleteAttrInfo(baseAttrInfo);

        return "success";
    }

    @ResponseBody
    @RequestMapping("updateAttr")
    public String updateAttr(BaseAttrInfo baseAttrInfo){
        attrService.updateAttr(baseAttrInfo);
        return "success";
    }
}