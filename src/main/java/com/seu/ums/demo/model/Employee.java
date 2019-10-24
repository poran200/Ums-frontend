package com.seu.ums.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

@EqualsAndHashCode(of = {"initial"})
public class Employee {

    private String initial;
    private String name;
    private String email;
    private String program;
    private String loginpassword;

    private  Role  role;

}
