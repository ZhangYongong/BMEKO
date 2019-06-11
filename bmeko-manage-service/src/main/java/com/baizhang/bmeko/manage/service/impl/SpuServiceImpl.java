package com.baizhang.bmeko.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baizhang.bmeko.bean.*;
import com.baizhang.bmeko.manage.mapper.*;
import com.baizhang.bmeko.service.SpuService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


@Service
public class SpuServiceImpl implements SpuService {

    @Autowired
    private SpuInfoMapper spuInfoMapper;

    @Autowired
    private BaseSaleAttrMapper baseSaleAttrMapper;

    @Autowired
    private SpuSaleAttrMapper spuSaleAttrMapper;

    @Autowired
    private SpuSaleAttrValueMapper spuSaleAttrValueMapper;

    @Autowired
    private SpuImageMapper spuImageMapper ;




    @Override
    public List<SpuInfo> getSpuInfoList(String catalog3Id) {
        SpuInfo spuInfo = new SpuInfo();
        spuInfo.setCatalog3Id(catalog3Id);

        return spuInfoMapper.select(spuInfo);
    }

    @Override
    public List<BaseSaleAttr> baseSaleAttrList() {


        return baseSaleAttrMapper.selectAll();
    }

    @Override
    public void saveSpu(SpuInfo spuInfo) {
        //保存spu信息
        spuInfoMapper.insert(spuInfo);
        String spuId = spuInfo.getId();

        //保存图片信息
        List<SpuImage> spuImageList = spuInfo.getSpuImageList();
        for (SpuImage spuImage : spuImageList) {
            spuImage.setSpuId(spuId);
            spuImageMapper.insert(spuImage);
        }


        List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();

        for (SpuSaleAttr spuSaleAttr : spuSaleAttrList) {
            spuSaleAttr.setSpuId(spuId);

            spuSaleAttrMapper.insert(spuSaleAttr);

            List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttr.getSpuSaleAttrValueList();

            for (SpuSaleAttrValue spuSaleAttrValue : spuSaleAttrValueList) {
                spuSaleAttrValue.setSpuId(spuId);
                spuSaleAttrValueMapper.insert(spuSaleAttrValue);
            }
        }
    }

    @Override
    public List<SpuSaleAttr> getSaleAttrListBySpuId(String spuId) {
        SpuSaleAttr spuSaleAttr = new SpuSaleAttr();
        spuSaleAttr.setSpuId(spuId);
        List<SpuSaleAttr> spuSaleAttrs = spuSaleAttrMapper.select(spuSaleAttr);

        SpuSaleAttrValue spuSaleAttrValue = new SpuSaleAttrValue();
        for (SpuSaleAttr saleAttr : spuSaleAttrs) {
            String saleAttrId = saleAttr.getSaleAttrId();
            spuSaleAttrValue.setSaleAttrId(saleAttrId);
            spuSaleAttrValue.setSpuId(spuId);

            saleAttr.setSpuSaleAttrValueList(spuSaleAttrValueMapper.select(spuSaleAttrValue));

        }
        return spuSaleAttrs;
    }

    @Override
    public List<SpuImage> getSpuImageListBySpuId(String spuId) {

        SpuImage spuImage = new SpuImage();
        spuImage.setSpuId(spuId);
        List<SpuImage> spuImageList = spuImageMapper.select(spuImage);

        return spuImageList;
    }

    @Override
    public List<SkuInfo> getSkuSaleAttrValueListBySpu(String spuId) {
        return spuSaleAttrValueMapper.selectSkuSaleAttrValueListBySpu(spuId);
    }
}
