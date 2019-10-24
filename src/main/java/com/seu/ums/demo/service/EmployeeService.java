package com.seu.ums.demo.service;

import com.seu.ums.demo.model.Employee;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class EmployeeService  {
    @Value("${hrmUrl}/employee")
    private String baseUrl;
    private RestTemplate restTemplate;

    public EmployeeService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Employee>  getEmployees(){
        ResponseEntity<List<Employee>> responseEntity = restTemplate.exchange(baseUrl, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Employee>>() {});
        return responseEntity.getBody();
    }
    public List<Employee> getEmployeeByProgramName(String programName){
        ResponseEntity<List<Employee>> responseEntity = restTemplate.exchange(baseUrl + "/prgramName/" + programName, HttpMethod.GET
                , null, new ParameterizedTypeReference<List<Employee>>() {});
        return  responseEntity.getBody();
    }
    public Employee getEmployeeById(String initial){
        return  restTemplate.getForObject(baseUrl+"/"+initial,Employee.class);
    }
    public Employee create(Employee employee){
        return restTemplate.postForObject(baseUrl,employee,Employee.class);
    }
    public  Employee update(Employee employee){
        HttpEntity<Employee> reqEmloyee= new HttpEntity<>(employee);
        restTemplate.exchange(baseUrl+"/"+employee.getInitial(),HttpMethod.PUT,reqEmloyee,Employee.class);
        return reqEmloyee.getBody();
    }

    public boolean  delete(String id){
        restTemplate.delete(baseUrl+"/"+id);
        return true;
    }
}
