package com.baizhang.bmeko.bean.enums;

/**
 * @author YONGHONG ZHANG
 * @date 2019-05-28-19:33
 */
public enum PaymentWay {
    ONLINE("在线支付"),
    OUTLINE("货到付款" );

    private String comment ;


    PaymentWay(String comment ){
        this.comment=comment;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
