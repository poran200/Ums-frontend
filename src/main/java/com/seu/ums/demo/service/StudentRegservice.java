package com.seu.ums.demo.service;

import com.seu.ums.demo.model.Scetion;
import com.seu.ums.demo.model.StudentInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class StudentRegservice {
    @Value("${regUrl}/registration")
    private String baseUrl;
    @Value("${regUrl}/student")
    private String url ;
    private RestTemplate restTemplate;

    public StudentRegservice(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    public List<Scetion> getStudentSections (String studentId){
        ResponseEntity<List<Scetion>> responseEntity= restTemplate.exchange(url+"/"+studentId,
                HttpMethod.GET,null,
                new ParameterizedTypeReference<List<Scetion>>() {});

        List<Scetion>  sectionList = responseEntity.getBody();
        return   sectionList;

    }


    public List<StudentInfo> findAllRegisterStudent(){
        ResponseEntity<List<StudentInfo>> responseEntity= restTemplate.exchange(baseUrl,
                HttpMethod.GET,null,
                new ParameterizedTypeReference<List<StudentInfo>>() {});

        List<StudentInfo> studentInfos = responseEntity.getBody();
        return  studentInfos;

    }

    public  StudentInfo regforScetion(String scetionId, StudentInfo studentInfo){
          return  restTemplate.postForObject(baseUrl+"/"+scetionId,studentInfo,StudentInfo.class);
    }

    public String dropScetion(String scetionId,String studentId){
        restTemplate.delete(baseUrl+"/"+scetionId+"/"+studentId,StudentInfo.class);
        return "Drop "+scetionId;
    }


}
