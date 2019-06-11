package com.baizhang.bmeko.user.mapper;

import com.baizhang.bmeko.bean.UserInfo;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author YONGHONG ZHANG
 * @date 2019-05-27-11:13
 */
//继承了通用Mapper之后就不需要写sql语句
public interface UserInfoMapper extends Mapper<UserInfo> {

}
