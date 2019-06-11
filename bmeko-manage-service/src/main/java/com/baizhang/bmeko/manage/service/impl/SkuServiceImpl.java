package com.baizhang.bmeko.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.baizhang.bmeko.bean.SkuAttrValue;
import com.baizhang.bmeko.bean.SkuImage;
import com.baizhang.bmeko.bean.SkuInfo;
import com.baizhang.bmeko.bean.SkuSaleAttrValue;
import com.baizhang.bmeko.manage.mapper.SkuAttrValueMapper;
import com.baizhang.bmeko.manage.mapper.SkuImageMapper;
import com.baizhang.bmeko.manage.mapper.SkuInfoMapper;
import com.baizhang.bmeko.manage.mapper.SkuSaleAttrValueMapper;
import com.baizhang.bmeko.service.SkuService;
import com.baizhang.bmeko.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.math.BigDecimal;
import java.util.List;

@Service
public class SkuServiceImpl implements SkuService {

    @Autowired
    private SkuInfoMapper skuInfoMapper;

    @Autowired
    private SkuAttrValueMapper skuAttrValueMapper;

    @Autowired
    private SkuSaleAttrValueMapper skusaleAttrValueMapper;

    @Autowired
    private SkuImageMapper skuImageMapper;

    @Autowired
    private RedisUtil redisUtil;

    public SkuServiceImpl(){

    }

    @Override
    public List<SkuInfo> getSkuListBySpu(String spuId) {
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setSpuId(spuId);
        return skuInfoMapper.select(skuInfo);
    }

    @Override
    public void saveSku(SkuInfo skuInfo) {

        skuInfoMapper.insert(skuInfo);
        String skuId = skuInfo.getId();

        //保存平台属性
        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        for (SkuAttrValue skuAttrValue : skuAttrValueList) {
            skuAttrValue.setSkuId(skuId);
            skuAttrValueMapper.insert(skuAttrValue);
        }

        //保存销售属性
        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {
            skuSaleAttrValue.setSkuId(skuId);
            skusaleAttrValueMapper.insert(skuSaleAttrValue);
        }

        //保存图片信息skuImageMapper
        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        for (SkuImage skuImage : skuImageList) {
            skuImage.setSkuId(skuId);
            skuImageMapper.insert(skuImage);
        }

    }

    @Override
    public SkuInfo getSkuById(String skuId) {
        SkuInfo skuInfo = null;
        Jedis jedis = redisUtil.getJedis();
        //查询redis缓存
        String key ="sku:"+skuId +":info";
        String val = jedis.get(key);

        if("empty".equals(val)){
            return skuInfo;
        }



        if(StringUtils.isBlank(val)){

            //申请缓存锁
            String OK = jedis.set("sku:" + skuId + ":lock", "1", "nx", "px", 3000);

            if("OK".equals(OK)){//拿到缓存锁
                //查询DB
                skuInfo = getSkuByIdFromDb(skuId);


                if(skuInfo!=null){
                    //同步缓存
                    jedis.set(key, JSON.toJSONString(skuInfo));
                }else {
                    //通知同伴
                    jedis.setex("sku:" + skuId + ":info",10,"empty");
                }
                //归还缓存锁
                jedis.del("sku:" + skuId + ":lock");

            }else{//没有拿到缓存锁
                //自旋
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                getSkuById(skuId);
            }
        }else {
            //正常转换缓存数据
            skuInfo = JSON.parseObject(val, SkuInfo.class);

        }



        return skuInfo;
    }

    @Override
    public List<SkuInfo> getSkuLIstByCatalog3Id(String catalog3Id) {
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setCatalog3Id(catalog3Id);
        List<SkuInfo> skuInfos = skuInfoMapper.select(skuInfo);

        for (SkuInfo info : skuInfos) {
            String id = info.getId();

            SkuAttrValue skuAttrValue = new SkuAttrValue();
            skuAttrValue.setSkuId(id);
            List<SkuAttrValue> skuAttrValues = skuAttrValueMapper.select(skuAttrValue);


            info.setSkuAttrValueList(skuAttrValues);

        }

        return skuInfos;
    }

    @Override
    public boolean checkPrice(BigDecimal skuPrice, String skuId) {

        boolean b = false;

        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setId(skuId);
        SkuInfo sku = skuInfoMapper.selectOne(skuInfo);

        if(sku!=null){
            int i = sku.getPrice().compareTo(skuPrice);
            if(i==0){
                b=true;
            }
        }


        return b;
    }


    public SkuInfo getSkuByIdFromDb(String skuId){


        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setId(skuId);
        SkuInfo skuInfo1 = skuInfoMapper.selectOne(skuInfo);

        SkuImage skuImage = new SkuImage();
        skuImage.setSkuId(skuId);
        List<SkuImage> skuImages = skuImageMapper.select(skuImage);

        skuInfo1.setSkuImageList(skuImages);


        SkuSaleAttrValue skuSaleAttrValue = new SkuSaleAttrValue();
        skuSaleAttrValue.setSkuId(skuId);
        List<SkuSaleAttrValue> skuSaleAttrValues = skusaleAttrValueMapper.select(skuSaleAttrValue);
        skuInfo1.setSkuSaleAttrValueList(skuSaleAttrValues);

        return skuInfo1;
    }
}
