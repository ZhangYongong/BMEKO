package com.baizhang.bmeko.service;

import com.baizhang.bmeko.bean.BaseCatalog1;
import com.baizhang.bmeko.bean.BaseCatalog2;
import com.baizhang.bmeko.bean.BaseCatalog3;

import java.util.List;

/**
 * @author YONGHONG ZHANG
 * @date 2019-05-28-19:37
 */
public interface CatalogService {
    List<BaseCatalog1> getCatalog1();

    List<BaseCatalog2> getCatalog2(String catalog1Id);

    List<BaseCatalog3> getCatalog3(String catalog2Id);
}
