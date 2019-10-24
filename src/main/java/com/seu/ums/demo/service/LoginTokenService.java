package com.seu.ums.demo.service;

import com.seu.ums.demo.eception.ResourseNotFoundException;
import com.seu.ums.demo.model.LoginToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class LoginTokenService {
    @Value("${authUrl}/authorization")
    private String baseUrl;

    private RestTemplate restTemplate;

    public LoginTokenService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<LoginToken> findallToken(){
        ResponseEntity<List<LoginToken>> responseEntity =restTemplate.exchange(
                baseUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<LoginToken>>() {});
        List<LoginToken> studentList =responseEntity.getBody();
        return studentList;
    }

     public  LoginToken  findToken(String id){
        return  restTemplate.getForObject(baseUrl+"/"+id,LoginToken.class);
     }
     public  LoginToken createToken(LoginToken loginToken){
         return  restTemplate.postForObject(baseUrl,loginToken,LoginToken.class);
     }

     public LoginToken authentication(String user, String password) throws ResourseNotFoundException{

      LoginToken loginToken=  restTemplate.getForObject(baseUrl+"/"+user,LoginToken.class);
      if (loginToken.getLoginPassword().equals(password))
          return  loginToken;
      else {
          throw  new ResourseNotFoundException(loginToken.getUserId());
      }

     }
     public  LoginToken updateToken(LoginToken loginToken){
         HttpEntity<LoginToken> reqToken = new HttpEntity<>(loginToken);
          restTemplate.exchange(baseUrl+"/"+loginToken.getUserId(),HttpMethod.PUT,reqToken,LoginToken.class);
          return reqToken.getBody();
     }

//    public LoginToken updateToken(String username, String pssword) {
//        return null;
//    }
}
