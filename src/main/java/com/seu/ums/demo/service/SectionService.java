package com.seu.ums.demo.service;

import com.seu.ums.demo.eception.ResourseNotFoundException;
import com.seu.ums.demo.model.Scetion;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class SectionService {
    @Value("${regUrl}/section")
    private  String baseUrl;
    private RestTemplate restTemplate;

    public SectionService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Scetion>  getSectionList(){
        ResponseEntity<List<Scetion>> response = restTemplate.exchange(baseUrl, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Scetion>>() {
                });
        return  response.getBody();
    }

    public List<Scetion> findByFaculty(String facultyInitial){
          ResponseEntity<List<Scetion>> response = restTemplate.exchange(baseUrl + "/faulty/" + facultyInitial, HttpMethod.GET, null,
                  new ParameterizedTypeReference<List<Scetion>>() {});
           List<Scetion> scetionList = response.getBody();
           return scetionList;
    }
    public Scetion createSection (String courseCode, Scetion scetion) throws ResourseNotFoundException {

        Scetion scetion1= restTemplate.postForObject(baseUrl+"/"+courseCode,scetion, Scetion.class);
        if (scetion1==null){
           throw new ResourseNotFoundException( " Section Already exist");
        }
        System.out.println(scetion.toString());
        return scetion1;
    }
    public List<Scetion> findByCourseCode(String courseCode){
        ResponseEntity<List<Scetion>> response = restTemplate.exchange(baseUrl + "/coursecode/" + courseCode, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Scetion>>() {});
        List<Scetion> scetionList = response.getBody();
        return scetionList;
    }

    public List<Scetion> findByProgram(String programName){
        ResponseEntity<List<Scetion>> response = restTemplate.exchange(baseUrl + "/program/" +programName, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Scetion>>() {});
        List<Scetion> scetionList = response.getBody();
        return scetionList;
    }

    public Scetion updateSection (String sectionId, Scetion scetion){
        HttpEntity<Scetion> reqSection = new HttpEntity<>(scetion);
        restTemplate.exchange(baseUrl+"/id/"+sectionId,HttpMethod.PUT,reqSection, Scetion.class);
        return reqSection.getBody();
    }
    public String deleteSection (String sectionId){
        restTemplate.delete(baseUrl+"/"+sectionId);
        return  sectionId+"->Deleted ";
    }
}
