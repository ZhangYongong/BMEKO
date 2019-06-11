package com.baizhang.bmeko.bean.enums;

/**
 * @author YONGHONG ZHANG
 * @date 2019-05-28-19:32
 */
public enum PaymentStatus {
    UNPAID("支付中"),
    PAID("已支付"),
    PAY_FAIL("支付失败"),
    ClOSED("已关闭");

    private String name ;

    PaymentStatus(String name) {
        this.name=name;
    }
}
