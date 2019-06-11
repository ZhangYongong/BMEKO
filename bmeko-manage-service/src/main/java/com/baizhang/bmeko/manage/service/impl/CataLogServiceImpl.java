package com.baizhang.bmeko.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baizhang.bmeko.bean.BaseCatalog1;
import com.baizhang.bmeko.bean.BaseCatalog2;
import com.baizhang.bmeko.bean.BaseCatalog3;
import com.baizhang.bmeko.manage.mapper.BaseCatalog1Mapper;
import com.baizhang.bmeko.manage.mapper.BaseCatalog2Mapper;
import com.baizhang.bmeko.manage.mapper.BaseCatalog3Mapper;
import com.baizhang.bmeko.service.CatalogService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author YONGHONG ZHANG
 * @date 2019-06-01-15:54
 */
@Service
public class CataLogServiceImpl implements CatalogService {

    @Autowired
    private BaseCatalog1Mapper baseCatalog1Mapper;

    @Autowired
    private BaseCatalog2Mapper baseCatalog2Mapper;

    @Autowired
    private BaseCatalog3Mapper baseCatalog3Mapper;

    @Override
    public List<BaseCatalog1> getCatalog1() {
        return baseCatalog1Mapper.selectAll();
    }

    @Override
    public List<BaseCatalog2> getCatalog2(String catalog1Id) {
        BaseCatalog2 baseCatalog2 = new BaseCatalog2();
        baseCatalog2.setCatalog1Id(catalog1Id);
        return baseCatalog2Mapper.select((baseCatalog2));
    }

    @Override
    public List<BaseCatalog3> getCatalog3(String catalog2Id) {
        BaseCatalog3 baseCatalog3 = new BaseCatalog3();
        baseCatalog3.setCatalog2Id(catalog2Id);
        return baseCatalog3Mapper.select(baseCatalog3);
    }


}
