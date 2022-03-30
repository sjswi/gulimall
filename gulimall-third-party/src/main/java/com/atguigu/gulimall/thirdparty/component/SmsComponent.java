package com.atguigu.gulimall.thirdparty.component;

import com.atguigu.gulimall.thirdparty.utils.HttpUtils;
import lombok.Data;
import org.apache.http.HttpResponse;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: gulimall
 * @description: 发送验证码
 * @author: yuxiaobing
 * @mail：a17281293@gmail.com
 * @date: 2022-03-16 19:33
 **/
@Data
@Component
@ConfigurationProperties("spring.cloud.alicloud.sms")
public class SmsComponent {
    private String host;
    private String path;
    private String templateId;
    private String appCode;

    public void sendSmsCode(String phone, String code){
//        String host = "https://jumsendsms.market.alicloudapi.com";
//        String path = "/sms/send-upgrade";
        String method = "POST";
//        String appcode = "43fee8116ed84548a58133b3a915ae62";
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + this.appCode);
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("phone_number", phone);
        querys.put("template_id", this.templateId);
        querys.put("content", "code:"+code);
        Map<String, String> bodys = new HashMap<>();


        try {
            /**
             * 重要提示如下:
             * HttpUtils请从
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java
             * 下载
             *
             * 相应的依赖请参照
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
             */
            HttpResponse response = HttpUtils.doPost(host, path, method, headers, bodys, querys);
            System.out.println(response.toString());
            //获取response的body
            //System.out.println(EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
