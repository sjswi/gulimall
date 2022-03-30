package com.atguigu.common.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * @program: gulimall
 * @description: 用户注册
 * @author: yuxiaobing
 * @mail：a17281293@gmail.com
 * @date: 2022-03-16 21:03
 **/
@Data
public class MemberRegister {
    @NotEmpty(message = "用户名必须提交")
    @Length(min=6,max=18,message="用户名必须是6-18位字符")
    private String userName;
    @NotEmpty(message = "密码必须提交")
    @Length(min=6,max=18,message="用户名必须是6-18位字符")
    private String password;
    @Pattern(regexp = "^[1]([3-9])[0-9]{9}$", message = "手机号格式不正确")
    private String phone;
}
