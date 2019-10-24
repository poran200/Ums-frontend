package com.seu.ums.demo.service;

import com.seu.ums.demo.model.Course;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class CourseService {
    @Value("${regUrl}/course")
    private String baseUrl;
    private RestTemplate restTemplate;

    public CourseService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Course> findall(){
        ResponseEntity<List<Course>> responseEntity = restTemplate.exchange(baseUrl,
                HttpMethod.GET,
                null,
               new ParameterizedTypeReference<List<Course>>(){});
         List<Course> courseList = responseEntity.getBody();
         return courseList;
    }

    public Course createCourse(String programName,Course course){
        return restTemplate.postForObject(baseUrl+"/"+programName,course,Course.class);
    }

    public  List<Course> findByProgram(String programName){
        ResponseEntity<List<Course>> responseEntity = restTemplate.exchange(baseUrl + "/programName/" + programName,
                HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Course>>() {});

        return responseEntity.getBody();
    }

    public List<Course> findBycourseTitle(String conursetitle){
         ResponseEntity<List<Course>> responseEntity = restTemplate.exchange(
                 baseUrl + "/title" + conursetitle, HttpMethod.GET, null
                 , new ParameterizedTypeReference<List<Course>>() {});
          return responseEntity.getBody();
    }

    public List<Course> findBycoursecode(String conursecode){
        ResponseEntity<List<Course>> responseEntity = restTemplate.exchange(
                baseUrl + "/code" + conursecode, HttpMethod.GET, null
                , new ParameterizedTypeReference<List<Course>>() {});
        return responseEntity.getBody();
    }


}
