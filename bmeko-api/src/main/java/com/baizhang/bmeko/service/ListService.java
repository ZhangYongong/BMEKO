package com.baizhang.bmeko.service;

import com.baizhang.bmeko.bean.SkuLsInfo;
import com.baizhang.bmeko.bean.SkuLsParam;

import java.util.List;

/**
 * @author YONGHONG ZHANG
 * @date 2019-06-01-12:02
 */
public interface ListService {
    List<SkuLsInfo> search(SkuLsParam skuLsParam);
}
