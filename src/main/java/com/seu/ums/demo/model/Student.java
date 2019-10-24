package com.seu.ums.demo.model;

import lombok.*;


import javax.validation.constraints.NotNull;
import java.time.LocalDate;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class Student {

    private long id;
    @NotNull
    private String name;
    @NotNull
    private LocalDate dob;
    private String email;
    private int batch;
    @NotNull
    private String program;




    //    @ElementCollection
//    private List<String> phonelist = new ArrayList<>();
//    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
//    @JoinColumn(name = "student_id")
//    @NotNull
//    private List<Address> addresses;

//    public void add(Address temaddress) {
//        if (addresses == null) {
//            addresses = new ArrayList<>();
//        }
//        addresses.add(temaddress);
//    }



}
