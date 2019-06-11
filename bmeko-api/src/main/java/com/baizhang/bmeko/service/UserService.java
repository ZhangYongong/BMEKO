package com.baizhang.bmeko.service;


import com.baizhang.bmeko.bean.UserAddress;
import com.baizhang.bmeko.bean.UserInfo;

import java.util.List;

/**
 * @author YONGHONG ZHANG
 * @date 2019-05-27-11:00
 */
public interface UserService {
    public List<UserInfo> infoList();

    public void addInfo(UserInfo userInfo);

    public void updateInfo(UserInfo userInfo);

    public UserInfo infoOne(String id);

    public void delInfo(String id);
    public List<UserAddress> addressList();
    public UserAddress addressOne(String id);
    public void updateAddress(UserAddress address);
    public void addAddress(UserAddress address);

    public void delAddress(String id);

    UserInfo login(UserInfo userInfo);

    List<UserAddress> getUserAddressList(String userId);

}
