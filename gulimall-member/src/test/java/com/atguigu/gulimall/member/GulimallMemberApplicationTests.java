package com.atguigu.gulimall.member;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

@SpringBootTest
class GulimallMemberApplicationTests {

    @Test
    void contextLoads() {
        BCryptPasswordEncoder cryptPasswordEncoder = new BCryptPasswordEncoder();
        String encode = cryptPasswordEncoder.encode("123456");
        boolean matches = cryptPasswordEncoder.matches("123456", "$2a$10$2.2dO2ibg.sXSjht6EIFcOZpB4WCnspJ7LjGCqhUnpOi.ezUUCD0u");
//        String s = DigestUtils.md5DigestAsHex("123456".getBytes(StandardCharsets.UTF_8));
        System.out.println(matches);
    }


}
