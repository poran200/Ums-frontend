package com.seu.ums.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"code"})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
//@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id", scope = Scetion.class)

public class Course implements Serializable {
    private static final long serialVersionUID = 1L;

    private String code;
    @NotNull
    private String title;
    @NotNull
    private int credit;
    @NotNull
    private String program;


    @JsonIgnoreProperties("course")
    private List<Scetion> scetionList;

    public Course(String code, String title) {
    }

    public Course(String code, @NotNull String title, @NotNull int credit, String program) {
        this.code = code;
        this.title = title;
        this.credit = credit;
        this.program = program;

    }



}
