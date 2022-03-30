package com.atguigu.common.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @program: gulimall
 * @description: 响应数据Vo
 * @author: yuxiaobing
 * @mail：a17281293@gmail.com
 * @date: 2022-03-21 11:55
 **/
@Data
public class MemberResponseVo implements Serializable {

    private Long id;
    /**
     * ??Ա?ȼ?id
     */
    private Long levelId;
    /**
     * ?û???
     */
    private String username;
    /**
     * ???
     */
    private String password;
    /**
     * ?ǳ
     */
    private String nickname;
    /**
     * ?ֻ????
     */
    private String mobile;
    /**
     * ???
     */
    private String email;
    /**
     * ͷ?
     */
    private String header;
    /**
     * ?Ա
     */
    private Integer gender;
    /**
     * ???
     */
    private Date birth;
    /**
     * ???ڳ??
     */
    private String city;
    /**
     * ְҵ
     */
    private String job;
    /**
     * ????ǩ??
     */
    private String sign;
    /**
     * ?û???Դ
     */
    private Integer sourceType;
    /**
     * ???
     */
    private Integer integration;
    /**
     * ?ɳ?ֵ
     */
    private Integer growth;
    /**
     * ????״̬
     */
    private Integer status;
    /**
     * ע??ʱ?
     */
    private Date createTime;

    private String socialUid;

    private String accessToken;

    private Long expiresIn;
}
