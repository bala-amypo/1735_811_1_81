package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class User{

     @Id
     @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    private String fullname;
    private String email;
    private String department;
    private String role;
    private String password;
    private LocalDateTime:createdAt;


    public User(Long id,String fullname,String email,String department,
    String role,String password, LocalDateTime createdAt){
        this.fullname=fullname;
        this.email=email;
        this.department=department;
        this.role=role;
        this.password
    }
    public StudentEntity(){

    }
    public void setName(String name){
        this.name=name;
    }
    public String getName(){
        return this.name;
    }
    public void setEmail(){
        this.email=email;
    }
    public String getEmail(){
        return this.email;
    }
    public void setCgpa(){
        this.cgpa=cgpa;
    }
    public float getcgpa(){
        return this.cgpa;   
    }
}

