package com.sdProj.data;

import java.sql.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;


@Entity
public class Player {
    @Id
    private String name;
    @Column(nullable = false)
    private String position, photo;
    @Column(nullable = true)
    private Date birthDate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="team_name", nullable=false)
    private Team team;
    @OneToMany(
        targetEntity=com.sdProj.data.Event.class, 
        cascade=CascadeType.ALL,
        mappedBy="player")
    private List<Event> events;

    public Player() {
    }

    public Player(String name, String photo, String position, Date birthDate, Team team) {
        this.name = name;
        this.position = position;
        this.birthDate = birthDate;
        this.team = team;
        this.photo = photo;
    }

    public String getName() {
        return this.name;
    }

    public Team getTeam() { 
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return this.position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Date getBirthDate() {
        return this.birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getPhoto() {
        return this.photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    @Override
    public String toString() {
        return "{" +
            " name='" + getName() + "'" +
            ", position='" + getPosition() + "'" +
            ", birthDate='" + getBirthDate() + "'" +
            "}";
    }

    public List<Event> getEvents() {
        return events;
    }
    
    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public void addEvent(Event event) {
        this.events.add(event);
    }

    public void removeEvent(Event event) {
        this.events.remove(event);
    }

    public void clearEvents() {
        this.events.clear();
    }
   
}
/*
Example 2: One-to-Many association without using generics

    // In Customer class:

    @OneToMany(targetEntity=com.acme.Order.class, cascade=ALL,
                mappedBy="customer")
    public Set getOrders() { return orders; }

    // In Order class:

    @ManyToOne
    @JoinColumn(name="CUST_ID", nullable=false)
    public Customer getCustomer() { return customer; }

    One customer(team) has many orders(players)
    */