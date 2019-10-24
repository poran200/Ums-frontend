package com.seu.ums.demo.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"program_title"})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id", scope = Course.class)
public class Program implements Serializable {
    private static final long serialVersionUID = 1L;

    private String program_title;
    private double minimum_CGPA;
    private int minimum_credits;

    @JsonIgnore
    private List<Course> courses;

    public Program(String program_title, double minimum_CGPA, int minimum_credits) {
        this.program_title = program_title;
        this.minimum_CGPA = minimum_CGPA;
        this.minimum_credits = minimum_credits;
    }

    public void addCousrse(Course tempcourse) {
        if (courses == null) {
            courses = new ArrayList<>();
        }
        courses.add(tempcourse);
    }


}
