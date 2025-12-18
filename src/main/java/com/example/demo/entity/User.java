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
    

    public StudentEntity(Long id,String name,String email,float cgpa){
        //this.id=id;
        this.name=name;
        this.email=email;
        this.cgpa=cgpa;
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

