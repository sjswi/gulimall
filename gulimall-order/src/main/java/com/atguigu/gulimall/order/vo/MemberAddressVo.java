package com.atguigu.gulimall.order.vo;

import lombok.Data;

/**
 * @program: gulimall
 * @description: 会员的收获地址列表
 * @author: yuxiaobing
 * @mail：a17281293@gmail.com
 * @date: 2022-03-24 16:21
 **/
@Data
public class MemberAddressVo {

    private Long id;
    /**
     * member_id
     */
    private Long memberId;
    /**
     * ?ջ???????
     */
    private String name;
    /**
     * ?绰
     */
    private String phone;
    /**
     * ???????
     */
    private String postCode;
    /**
     * ʡ??/ֱϽ?
     */
    private String province;
    /**
     * ???
     */
    private String city;
    /**
     * ??
     */
    private String region;
    /**
     * ??ϸ??ַ(?ֵ?)
     */
    private String detailAddress;
    /**
     * ʡ???????
     */
    private String areacode;
    /**
     * ?Ƿ?Ĭ?
     */
    private Integer defaultStatus;
}
