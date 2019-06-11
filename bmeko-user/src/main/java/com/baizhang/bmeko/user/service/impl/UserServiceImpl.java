package com.baizhang.bmeko.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.baizhang.bmeko.bean.UserAddress;
import com.baizhang.bmeko.service.UserService;
import com.baizhang.bmeko.bean.UserInfo;
import com.baizhang.bmeko.user.mapper.UserAddressMapper;
import com.baizhang.bmeko.user.mapper.UserInfoMapper;
import com.baizhang.bmeko.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.util.List;

/**
 * @author YONGHONG ZHANG
 * @date 2019-05-27-11:11
 */
@Service
public class UserServiceImpl implements UserService{

    @Autowired
    UserInfoMapper userInfoMapper;

    @Autowired
    UserAddressMapper userAddressMapper;

    @Autowired
    RedisUtil redisUtil;


    @Override
    public List<UserInfo> infoList() {
        return userInfoMapper.selectAll();
    }

    @Override
    public void addInfo(UserInfo userInfo) {
        userInfoMapper.insert(userInfo);
    }

    @Override
    public void updateInfo(UserInfo userInfo) {
        userInfoMapper.updateByPrimaryKey(userInfo);
    }

    @Override
    public UserInfo infoOne(String id) {
        return userInfoMapper.selectByPrimaryKey(id);
    }

    @Override
    public void delInfo(String id) {
        userInfoMapper.deleteByPrimaryKey(id);
    }

    @Override
    public List<UserAddress> addressList() {
        return userAddressMapper.selectAll();
    }

    @Override
    public UserAddress addressOne(String id) {
        return userAddressMapper.selectByPrimaryKey(id);
    }

    @Override
    public void updateAddress(UserAddress address) {
        userAddressMapper.updateByPrimaryKey(address);
    }

    @Override
    public void addAddress(UserAddress address) {
        userAddressMapper.insert(address);
    }

    @Override
    public void delAddress(String id) {
        userAddressMapper.deleteByPrimaryKey(id);
    }

    @Override
    public UserInfo login(UserInfo userInfo) {

        UserInfo user = userInfoMapper.selectOne(userInfo);

        if(user!=null){
            //同步缓存
            Jedis jedis = redisUtil.getJedis();
            jedis.set("user:"+user.getId()+":info", JSON.toJSONString(user));

            jedis.close();
        }

        return user;
    }

    @Override
    public List<UserAddress> getUserAddressList(String userId) {
        UserAddress address = new UserAddress();
        address.setUserId(userId);
        List<UserAddress> userAddresses = userAddressMapper.select(address);
        return userAddresses;
    }


}
