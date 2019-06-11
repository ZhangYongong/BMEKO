package com.baizhang.bmeko.list.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baizhang.bmeko.bean.*;
import com.baizhang.bmeko.service.AttrService;
import com.baizhang.bmeko.service.ListService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

/**
 * @author YONGHONG ZHANG
 * @date 2019-06-01-11:40
 */
@Controller
public class ListController {

    @Reference
    ListService listService;

    @Reference
    AttrService attrService;



    @RequestMapping("list.html")
    public String search(SkuLsParam skuLsParam, ModelMap map){

        List<SkuLsInfo> skuLsInfos = listService.search(skuLsParam);


        //封装平台属性的列表,排除已经选中的属性
        List<BaseAttrInfo> baseAttrInfos = getAttrValueIds(skuLsInfos);
        String[] valueId = skuLsParam.getValueId();
        List<Crumb> crumbs = new ArrayList<>();//面包屑

        if(valueId!=null&&valueId.length>0){
            for (String s : valueId) {
                Iterator<BaseAttrInfo> iterator = baseAttrInfos.iterator();

                Crumb crumb = new Crumb();
                //制作面包屑url
                String urlParamforCrumb = getUrlParamforCrumb(skuLsParam, s);
                //制作面包屑name
                String valueName = "";

                while (iterator.hasNext()){
                    BaseAttrInfo baseAttrInfo = iterator.next();
                    List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();

                    for (BaseAttrValue baseAttrValue : attrValueList) {

                        if (baseAttrValue.getId().equals(s)){

                            valueName = baseAttrValue.getValueName();
                            //封装面包屑
                            crumb.setValueName(valueName);
                            crumb.setUrlParam(urlParamforCrumb);
                            crumbs.add(crumb);
                            iterator.remove();
                        }


                    }
                }

            }
        }


        String urlParam = getUrlParam(skuLsParam);
        map.put("attrValueSelectedList",crumbs);
        map.put("urlParam",urlParam);
        map.put("attrList",baseAttrInfos);
        map.put("skuLsInfoList",skuLsInfos);

        return "list";
    }

    //面包屑URL
    private String getUrlParamforCrumb(SkuLsParam skuLsParam,String id) {

        String urlParam = "";

        String[] valueId = skuLsParam.getValueId();
        String keyword = skuLsParam.getKeyword();
        String catalog3Id = skuLsParam.getCatalog3Id();

        if(StringUtils.isNotBlank(keyword)){

            if(StringUtils.isBlank(urlParam)){
                urlParam = "keyword=" + keyword;
            }else {
                urlParam = urlParam + "&keyword=" + keyword;
            }
        }

        if(StringUtils.isNotBlank(catalog3Id)){
            if(StringUtils.isBlank(urlParam)){
                urlParam = "catalog3Id=" + catalog3Id;
            }else {
                urlParam = urlParam + "&catalog3Id" + catalog3Id;
            }
        }

        if(valueId!=null&&valueId.length>0){
            for (String s : valueId) {
                if(!id.equals(s)){
                    urlParam = urlParam + "&valueId=" + s;
                }
            }
        }


        return urlParam;
    }

    //制作普通url
    private String getUrlParam(SkuLsParam skuLsParam) {

        String urlParam = "";

        String[] valueId = skuLsParam.getValueId();
        String keyword = skuLsParam.getKeyword();
        String catalog3Id = skuLsParam.getCatalog3Id();

        if(StringUtils.isNotBlank(keyword)){

            if(StringUtils.isBlank(urlParam)){
                urlParam = "keyword=" + keyword;
            }else {
                urlParam = urlParam + "&keyword=" + keyword;
            }
        }

        if(StringUtils.isNotBlank(catalog3Id)){
            if(StringUtils.isBlank(urlParam)){
                urlParam = "catalog3Id=" + catalog3Id;
            }else {
                urlParam = urlParam + "&catalog3Id" + catalog3Id;
            }
        }

        if(valueId!=null&&valueId.length>0){
            for (String s : valueId) {
                urlParam = urlParam + "&valueId=" + s;
            }
        }


        return urlParam;
    }

    //sku平台属性列表
    private List<BaseAttrInfo> getAttrValueIds(List<SkuLsInfo> skuLsInfos) {

        Set<String> valueIds = new HashSet<>();
        for (SkuLsInfo skuLsInfo : skuLsInfos) {
            List<SkuLsAttrValue> skuAttrValueList = skuLsInfo.getSkuAttrValueList();

            for (SkuLsAttrValue skuLsAttrValue : skuAttrValueList) {
                String valueId = skuLsAttrValue.getValueId();

                valueIds.add(valueId);
            }
        }


        //根据去重后的id集合检索，关联到的平台属性列表
        List<BaseAttrInfo> baseAttrInfos = new ArrayList<>();
        baseAttrInfos = attrService.getAttrListByValueIds(valueIds);
        return baseAttrInfos;
    }

    @RequestMapping("index")
    public String index(){
        return "index";
    }

}
