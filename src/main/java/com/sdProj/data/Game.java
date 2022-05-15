package com.sdProj.data;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;

@Entity
public class Game implements Serializable{
    @Id
    @SequenceGenerator(
        name="event_id_seq", 
        sequenceName="event_id_seq", 
        allocationSize=1) 
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "event_id_seq") 
    private int id;
    private String location;
    private int awayGoals, homeGoals;
    private String winner = "TBD";
    @Column(nullable = false)
    private Timestamp gameDate;
    @ManyToMany(cascade = CascadeType.ALL)
    private List<Team> teams;
    @OneToMany(
        targetEntity=com.sdProj.data.Event.class, 
        cascade=CascadeType.ALL,
        mappedBy="game")
    private List<Event> events;

    public Game() {
    }

    public Game(String location, Timestamp gameDate, String winner, Team team1, Team team2, int homeGoals, int awayGoals) {
        this.location = location;
        this.gameDate = gameDate;
        this.winner =  winner; 
        this.teams = new ArrayList<>();
        this.teams.add(team1);
        this.teams.add(team2);
        this.events = new ArrayList<>();
        this.awayGoals = awayGoals;
        this.homeGoals = homeGoals;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Timestamp getGameDate() {
        return this.gameDate;
    }

    public void setGameDate(Timestamp gameDate) {
        this.gameDate = gameDate;
    }

    public List<Team> getTeams() {
        return this.teams;
    }

    public void addTeam(Team team) {
        this.teams.add(team);
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }

    @Override
    public String toString() {
        return "{" +
            " location='" + getLocation() + "'" +
            ", gameDate='" + getGameDate() + "'" +
            ", teams='" + getTeams() + "'" +
            ", winner='" + getWinner() + "'" +
            ", homeGoals='" + getHomeGoals() + "'" +
            ", awayGoals='" + getAwayGoals() + "'" +
            "}";
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getAwayGoals() {
        return awayGoals;
    }

    public void setAwayGoals(int awayGoals) {
        this.awayGoals = awayGoals;
    }

    public int getHomeGoals() {
        return homeGoals;
    }

    public void setHomeGoals(int homeGoals) {
        this.homeGoals = homeGoals;
    }

}
/*
Example 1: One-to-Many association using generics

    // In Customer class:

    @OneToMany(cascade=ALL, mappedBy="customer")
    public Set<Order> getOrders() { return orders; }

    In Order class:

    @ManyToOne
    @JoinColumn(name="CUST_ID", nullable=false)
     public Customer getCustomer() { return customer; }


Example 2: One-to-Many association without using generics
//One customer has many orders
    // In Customer class:
    @OneToMany(targetEntity=com.acme.Order.class, cascade=ALL,
                mappedBy="customer")
    public Set getOrders() { return orders; }

    // In Order class:

    @ManyToOne
    @JoinColumn(name="CUST_ID", nullable=false)
    public Customer getCustomer() { return customer; }


 Example 3: Unidirectional One-to-Many association using a foreign key mapping

    // In Customer class:

    @OneToMany(orphanRemoval=true)
    @JoinColumn(name="CUST_ID") // join column is in table for Order
    public Set<Order> getOrders() {return orders;} */