package com.seu.ums.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@AllArgsConstructor
@NoArgsConstructor
public class Grade {

    private   int id;
    private  long studentId;
    private String coursecode;
    private  double grade;
    private  int semNumber;
}
