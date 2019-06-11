package com.baizhang.bmeko.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baizhang.bmeko.bean.UserAddress;
import com.baizhang.bmeko.bean.UserInfo;
import com.baizhang.bmeko.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author YONGHONG ZHANG
 * @date 2019-05-27-10:53
 */
@RestController
@RequestMapping("user")
public class UserController {

    @Reference          //远程注入
    private UserService userService;


    @RequestMapping("infoList")
    public List<UserInfo> infoList(){
        List<UserInfo> infoList = userService.infoList();
        return infoList;
    }

    @RequestMapping("infoOne/{id}")
    public UserInfo infoOne(@PathVariable String id){
        UserInfo userInfo = userService.infoOne(id);
        return userInfo;
    }

    //   @RequestMapping("addUserInfo")
    //   public void addUserInfo(@RequestBody UserInfo userInfo){
//        UserInfo user = new UserInfo();
    //       user.setLoginName("aaa");
//        user.setPasswd("123");
    //       userService.addUserInfo(userInfo);
    //   }

    @RequestMapping("addInfo")
    public void addInfo(){
        UserInfo user = new UserInfo();
        user.setLoginName("bbb");
        user.setPasswd("456");
        userService.addInfo(user);
    }

    @RequestMapping("updateInfo")
    public void updateInfo(){
        UserInfo user = new UserInfo();
        user.setId("5");
        user.setLoginName("ccc");
        user.setPasswd("123");
        userService.updateInfo(user);
    }

    @RequestMapping("delInfo/{id}")
    public void delInfo(@PathVariable String id){

        userService.delInfo(id);
    }

    @RequestMapping("addressList")
    public List<UserAddress> addressList(){

        return userService.addressList();
    }

    @RequestMapping("addressOne/{id}")
    public UserAddress addressOne(@PathVariable String id){

        return userService.addressOne(id);
    }

    @RequestMapping("updateAddress")
    public void updateAddress(){
        UserAddress address = new UserAddress();
        address.setId("1");
        address.setUserId("5");
        address.setUserAddress("oui");
        userService.updateAddress(address);
    }

    @RequestMapping("addAddress")
    public void addAddress(){
        UserAddress address = new UserAddress();
        address.setUserId("4");
        address.setUserAddress("yyy");
        userService.addAddress(address);
    }

    @RequestMapping("delAddress/{id}")
    public void delAddress(@PathVariable String id){

        userService.delAddress(id);
    }
}

