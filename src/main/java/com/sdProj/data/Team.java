package com.sdProj.data;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

@Entity
public class Team {
    @Id 
    private String name;
    @Column(nullable = false)
    private String logo;
    @Column(nullable = false)
    private String stadium;
    @ManyToMany(mappedBy="teams")
    private List<Game> games;
    @OneToMany(
        targetEntity=com.sdProj.data.Player.class, 
        cascade=CascadeType.ALL,
        mappedBy="team")
    private List<Player> players;

    public Team() {
    }


    public Team(String name, String logo, String stadium) {
        this.name = name;
        this.logo = logo;
        this.stadium = stadium;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return this.logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
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

    public void removeGame(Game game) {
        this.games.remove(game);
    }

    public List<Player> getPlayers() { 
        return players; 
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public void addPlayer(Player player) {
        this.players.add(player);
    }

    public void removePlayer(Player player) {
        this.players.remove(player);
    }

    @Override
    public String toString() {
        return "{" +
            " name='" + getName() + "'" +
            ", logo='" + getLogo() + "'" +
            ", games='" + getGames() + "'" +
            "}";
    }

    public String getStadium() {
        return stadium;
    }

    public void setStadium(String stadium) {
        this.stadium = stadium;
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
