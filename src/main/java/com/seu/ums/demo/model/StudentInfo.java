package com.seu.ums.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;

@Getter
@Setter

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class StudentInfo {

    private  String id;
    private String name;



    private List<Scetion> scetions;

    public StudentInfo(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
