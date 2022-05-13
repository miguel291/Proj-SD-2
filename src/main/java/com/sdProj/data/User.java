package com.sdProj.data;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;


@Entity
public class User {
    @Id 
    private String username; 
    private long phone;
    @Column(nullable = false)
    private String name, password, role;
    @OneToMany(
        targetEntity=com.sdProj.data.Event.class, 
        cascade=CascadeType.ALL,
        mappedBy="user")
    private List<Event> events;

    public User() {
    }

    public User(int id, long phone, String name, String username, String password, String role) {
        this.phone = phone;
        this.name = name;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public long getPhone() {
        return this.phone;
    }

    public void setPhone(long phone) {
        this.phone = phone;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getusername() {
        return this.username;
    }

    public void setusername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return this.role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public User phone(long phone) {
        setPhone(phone);
        return this;
    }

    public User name(String name) {
        setName(name);
        return this;
    }

    public User username(String username) {
        setusername(username);
        return this;
    }

    public User password(String password) {
        setPassword(password);
        return this;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    @Override
    public String toString() {
        return "{" +
            ", phone='" + getPhone() + "'" +
            ", name='" + getName() + "'" +
            ", username='" + getusername() + "'" +
            ", password='" + getPassword() + "'" +
            ", admin='" + getRole() + "'" +
            "}";
    }
    
}