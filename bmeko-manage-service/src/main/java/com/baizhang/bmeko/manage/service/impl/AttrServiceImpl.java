package com.baizhang.bmeko.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baizhang.bmeko.bean.BaseAttrInfo;
import com.baizhang.bmeko.bean.BaseAttrValue;
import com.baizhang.bmeko.manage.mapper.BaseAttrInfoMapper;
import com.baizhang.bmeko.manage.mapper.BaseAttrValueMapper;
import com.baizhang.bmeko.service.AttrService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

@Service
public class AttrServiceImpl implements AttrService {

    @Autowired
    private BaseAttrInfoMapper baseAttrInfoMapper;

    @Autowired
    private BaseAttrValueMapper baseAttrValueMapper;


    @Override
    public List<BaseAttrInfo> getAttrList(String catalog3Id) {
        BaseAttrInfo baseAttrInfo = new BaseAttrInfo();
        baseAttrInfo.setCatalog3Id(catalog3Id);
        return baseAttrInfoMapper.select(baseAttrInfo);
    }

    @Override
    public void saveAttr(BaseAttrInfo baseAttrInfo) {
        baseAttrInfoMapper.insertSelective(baseAttrInfo);

        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
        for (BaseAttrValue baseAttrValue : attrValueList) {
            baseAttrValue.setAttrId(baseAttrInfo.getId());
            baseAttrValueMapper.insertSelective(baseAttrValue);
        }

    }

    @Override
    public List<BaseAttrValue> getAttrValueList(String attrId) {
        BaseAttrValue baseAttrValue = new BaseAttrValue();
        baseAttrValue.setAttrId(attrId);
        return baseAttrValueMapper.select(baseAttrValue);
    }

    @Override
    public void deleteAttrInfo(BaseAttrInfo baseAttrInfo) {
        BaseAttrValue baseAttrValue = new BaseAttrValue();
        baseAttrValue.setAttrId(baseAttrInfo.getId());
        baseAttrValueMapper.delete(baseAttrValue);

        baseAttrInfoMapper.delete(baseAttrInfo);
    }

    @Override
    public void updateAttr(BaseAttrInfo baseAttrInfo) {
        //修改属性
        baseAttrInfoMapper.updateByPrimaryKey(baseAttrInfo);
        //通过attrId查找所有的属性值
        BaseAttrValue baseAttrValue = new BaseAttrValue();
        baseAttrValue.setAttrId(baseAttrInfo.getId());
        List<BaseAttrValue> baseAttrValues = baseAttrValueMapper.select(baseAttrValue);
        //遍历所有的属性值，并删除
        for (BaseAttrValue baseAttrValue2 : baseAttrValues) {
            baseAttrValueMapper.delete(baseAttrValue2);
        }

        //保存新的属性值
        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
        for (BaseAttrValue baseAttrValue3 : attrValueList) {
            baseAttrValue3.setAttrId(baseAttrInfo.getId());
            baseAttrValueMapper.insertSelective(baseAttrValue3);
        }

    }

    @Override
    public List<BaseAttrInfo> getAttrListByCgt3Id(String catalog3Id) {
        BaseAttrInfo baseAttrInfo = new BaseAttrInfo();
        baseAttrInfo.setCatalog3Id(catalog3Id);
        List<BaseAttrInfo> baseAttrInfoList = baseAttrInfoMapper.select(baseAttrInfo);

        for (BaseAttrInfo attrInfo : baseAttrInfoList) {
            String attrId = attrInfo.getId();

            BaseAttrValue baseAttrValue = new BaseAttrValue();
            baseAttrValue.setAttrId(attrId);
            List<BaseAttrValue> baseAttrValueList = baseAttrValueMapper.select(baseAttrValue);

            attrInfo.setAttrValueList(baseAttrValueList);
        }

        return baseAttrInfoList;
    }

    @Override
    public List<BaseAttrInfo> getAttrListByValueIds(Set<String> valueIds) {
        String join = StringUtils.join(valueIds, ",");

        List<BaseAttrInfo> baseAttrInfos = baseAttrValueMapper.selectAttrListByValueIds(join);

        return baseAttrInfos;
    }


}
