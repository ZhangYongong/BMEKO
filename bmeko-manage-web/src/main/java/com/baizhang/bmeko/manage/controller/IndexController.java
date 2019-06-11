package com.baizhang.bmeko.manage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author YONGHONG ZHANG
 * @date 2019-05-29-15:12
 */
@Controller
public class IndexController {


    @RequestMapping("spuListPage")
    public String spuListPage(){
        return "spuListPage";
    }

    @RequestMapping("attrListPage")
    public String attrListPage(){
        return "attrListPage";
    }

    @RequestMapping("index")
    public String index(){
        return "index";
    }


}

