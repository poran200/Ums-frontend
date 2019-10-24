package com.seu.ums.demo;

import com.seu.ums.demo.model.LoginToken;
import com.seu.ums.demo.model.Role;
import com.seu.ums.demo.service.LoginTokenService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FrontendApplicationTests {
    @Autowired
    private LoginTokenService loginTokenService;
    @Test
    public void contextLoads() {
    }

    @Test
    public void  insertToken(){
        LoginToken loginToken = new LoginToken("sbb","sohel babu","123", Role.Faculty);



        LoginToken loginToken1 = loginTokenService.createToken(loginToken);
        System.out.println(loginToken1.toString());
    }

}
