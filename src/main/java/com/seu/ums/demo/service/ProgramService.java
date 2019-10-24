package com.seu.ums.demo.service;

import com.seu.ums.demo.model.Program;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class ProgramService {
    @Value("${regUrl}/program")
    private  String bseUrl;

    private RestTemplate restTemplate;

    public ProgramService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Program> getProgramList(){
        ResponseEntity<List<Program>> respons= restTemplate.exchange(bseUrl, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Program>>() {});
          return  respons.getBody();
    }
    public Program findById (String programName){
        return restTemplate.getForObject(bseUrl+"/"+programName,Program.class);
    }
    public  Program create (Program program){
         return restTemplate.postForObject(bseUrl,program,Program.class);
    }

    public Program updateProgram(String programName, Program program){
        HttpEntity<Program> reqUpdate = new HttpEntity<>(program);
        restTemplate.exchange(bseUrl+"/"+programName,HttpMethod.PUT,reqUpdate,Program.class);
        return reqUpdate.getBody();
    }
}
