package com.baizhang.bmeko.user.mapper;

import com.baizhang.bmeko.bean.UserAddress;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author YONGHONG ZHANG
 * @date 2019-05-28-20:48
 */
//继承了通用Mapper之后就不需要写sql语句
public interface UserAddressMapper extends Mapper<UserAddress> {
}
