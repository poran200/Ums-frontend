package com.seu.ums.demo.eception;

public class ResourseNotFoundException extends Exception {
    public ResourseNotFoundException(String message) {
        super(message+"not found !");
    }
}
