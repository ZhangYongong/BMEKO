package com.baizhang.bmeko.manage.mapper;


import com.baizhang.bmeko.bean.BaseAttrInfo;
import com.baizhang.bmeko.bean.BaseAttrValue;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BaseAttrValueMapper extends Mapper<BaseAttrValue> {
    List<BaseAttrInfo> selectAttrListByValueIds(@Param("ids") String join);
}
