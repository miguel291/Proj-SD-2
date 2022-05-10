package com.sdProj.data;

import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;


@Entity
public class Player {
    @Id
    private String name;
    private String position;
    private Date birthDate;
    private Team team;
    @ManyToOne
    @JoinColumn(name="TEAM_NAME", nullable=false)
    public Team getTeam() { return team;}


    public Player() {
    }

    public Player(String name, String position, Date birthDate) {
        this.name = name;
        this.position = position;
        this.birthDate = birthDate;
    }

    public String getName() {
        return this.name;
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

    @Override
    public String toString() {
        return "{" +
            " name='" + getName() + "'" +
            ", position='" + getPosition() + "'" +
            ", birthDate='" + getBirthDate() + "'" +
            "}";
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