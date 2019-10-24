package com.seu.ums.demo.service;

import com.seu.ums.demo.model.Student;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class StudentService {
    @Value("${addmissionUrl}/students")
    private String baseUrl;
    private RestTemplate restTemplate;

    public StudentService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Student> findall() {

        ResponseEntity<List<Student>> responseEntity =restTemplate.exchange(
                 baseUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Student>>() {});
             List<Student> studentList =responseEntity.getBody();
             return studentList;

    }

    public Student  create (Student student){
       return  restTemplate.postForObject(baseUrl,student,Student.class);
    }
    public  Student findbyid( long id){
        return  restTemplate.getForObject(baseUrl+"/"+id,Student.class);
    }
    public   Student  updateStudent( Student student){
        HttpEntity<Student> reqUpdate = new HttpEntity<>(student);
        restTemplate.exchange(baseUrl+"/"+student.getId(),HttpMethod.PUT,reqUpdate,Student.class);
        return  reqUpdate.getBody();
    }

}
