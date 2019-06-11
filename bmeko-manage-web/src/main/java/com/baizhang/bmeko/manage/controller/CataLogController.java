package com.baizhang.bmeko.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baizhang.bmeko.bean.BaseCatalog1;
import com.baizhang.bmeko.bean.BaseCatalog2;
import com.baizhang.bmeko.bean.BaseCatalog3;
import com.baizhang.bmeko.service.CatalogService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author YONGHONG ZHANG
 * @date 2019-05-29-15:18
 */
@Controller
public class CataLogController {

    @Reference
    private CatalogService cataLogService;


    @RequestMapping("getCatalog1")
    @ResponseBody
    public List<BaseCatalog1> getCatalog1(){

        List<BaseCatalog1> baseCatalog1s = cataLogService.getCatalog1();

        return baseCatalog1s;
    }
    @RequestMapping("getCatalog2")
    @ResponseBody
    public List<BaseCatalog2> getCatalog2(String catalog1Id){

        List<BaseCatalog2> baseCatalog2s = cataLogService.getCatalog2(catalog1Id);

        return baseCatalog2s;
    }

    @RequestMapping("getCatalog3")
    @ResponseBody
    public List<BaseCatalog3> getCatalog3(String catalog2Id){

        List<BaseCatalog3> baseCatalog3s = cataLogService.getCatalog3(catalog2Id);

        return baseCatalog3s;
    }
}
