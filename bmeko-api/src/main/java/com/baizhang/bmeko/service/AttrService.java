package com.baizhang.bmeko.service;

import com.baizhang.bmeko.bean.BaseAttrInfo;
import com.baizhang.bmeko.bean.BaseAttrValue;

import java.util.List;
import java.util.Set;

/**
 * @author YONGHONG ZHANG
 * @date 2019-05-28-19:37
 */
public interface AttrService {
    List<BaseAttrInfo> getAttrList(String catalog3Id);

    void saveAttr(BaseAttrInfo baseAttrInfo);

    List<BaseAttrValue> getAttrValueList(String attrId);

    void deleteAttrInfo(BaseAttrInfo baseAttrInfo);

    void updateAttr(BaseAttrInfo baseAttrInfo);

    List<BaseAttrInfo> getAttrListByCgt3Id(String catalog3Id);

    List<BaseAttrInfo> getAttrListByValueIds(Set<String> valueIds);
}
