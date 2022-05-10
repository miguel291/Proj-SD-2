package com.sdProj.data;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

@Entity
public class Student {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String name, telephone;
    private int age;
    @ManyToMany(cascade = CascadeType.ALL)
    private List<Professor> profs;

    public Student() {
    }

    public Student(String name, String telephone, int age) {
        this.name = name;
        this.telephone = telephone;
        this.age = age;
        this.profs = new ArrayList<>();
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTelephone() {
        return telephone;
    }

    public List<Professor> getProfs() {
        return profs;
    }

    public void setProfs(List<Professor> profs) {
        this.profs = profs;
    }

    public void addProf(Professor prof) {
        this.profs.add(prof);
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String toString() {
        return this.name + "(id = " + this.id + "). Telephone: " + this.telephone + ". Age: " + this.age;
    }
}
