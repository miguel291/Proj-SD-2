package com.sdProj.data;

import java.sql.Blob;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

@Entity
public class Team {
    @Id 
    private String name;
    private Blob image;
    @ManyToMany(mappedBy="teams")
    private List<Game> games;
    @OneToMany(targetEntity=com.sdProj.data.Player.class, cascade=CascadeType.ALL,
                mappedBy="team")
    public List<Player> getPlayers() { return players; }
    private List<Player> players;

    public Team() {
    }


    public Team(String name, Blob image) {
        this.name = name;
        this.image = image;
    }


    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Blob getImage() {
        return this.image;
    }

    public void setImage(Blob image) {
        this.image = image;
    }

    public List<Game> getGames() {
        return this.games;
    }

    public void setGames(List<Game> games) {
        this.games = games;
    }
    

    public void addGame(Game game) {
        this.games.add(game);
    }


    @Override
    public String toString() {
        return "{" +
            " name='" + getName() + "'" +
            ", image='" + getImage() + "'" +
            ", games='" + getGames() + "'" +
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
