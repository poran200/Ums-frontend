package com.seu.ums.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Data
@NoArgsConstructor
@EqualsAndHashCode(of = {"sectionID"})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

//@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id", scope = Course.class)


public class Scetion {

    private static final long serialVersionUID = 1L;
    @JsonIgnoreProperties("scetionList")
    private Course course;
//    private String courseCode;
//    private String courseTitle;
    private String faculty;
    private int section_number;
    private int capacity;
    private int semester_id;

    private String sectionID;

    @JsonIgnoreProperties("scetions")
    private List<StudentInfo> studentInfos;


    public void addStudent(StudentInfo tempStudentInfo) {
        if (studentInfos == null) {
            studentInfos = new ArrayList<>();
        }
        studentInfos.add(tempStudentInfo);

    }


//    public Section(Course course, String faculty, int section_number, int capacity, int semester_id) {
//        this.course = course;
//        this.faculty = faculty;
//        this.section_number = section_number;
//        this.capacity = capacity;
//        this.semester_id = semester_id;
//    }


    public Scetion( String faculty, int section_number, int capacity, int semester_id, String sectionID) {
        this.faculty = faculty;
        this.section_number = section_number;
        this.capacity = capacity;
        this.semester_id = semester_id;
        this.sectionID = sectionID;
    }

//    @Override
//    public String toString() {
//        return "Section{" +
//                "courseCode='" + courseCode + '\'' +
//                ", courseTitle='" + courseTitle + '\'' +
//                ", faculty='" + faculty + '\'' +
//                ", section_number=" + section_number +
//                ", capacity=" + capacity +
//                ", semester_id=" + semester_id +
//                ", sectionID='" + sectionID + '\'' +
//                ", students=" + studentInfos.stream().map(StudentInfo::getId) +
//                '}';
//    }
}
