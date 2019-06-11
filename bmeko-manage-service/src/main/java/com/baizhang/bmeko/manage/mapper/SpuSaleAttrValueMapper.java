package com.baizhang.bmeko.manage.mapper;


import com.baizhang.bmeko.bean.SkuInfo;
import com.baizhang.bmeko.bean.SpuSaleAttrValue;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SpuSaleAttrValueMapper extends Mapper<SpuSaleAttrValue> {
    List<SkuInfo> selectSkuSaleAttrValueListBySpu(@Param("spuId") String spuId);
}
