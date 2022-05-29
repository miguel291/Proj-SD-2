package com.sdProj.data;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

import org.hibernate.annotations.ManyToAny;

@Entity
public class Event {
    @Id 
    @SequenceGenerator(
        name="event_id_seq", 
        sequenceName="event_id_seq", 
        allocationSize=1) 
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "event_id_seq")
    private int id;
    private boolean valid = false;
    @Column(nullable = true)
    private Timestamp time;
    private String color;
    @Column(nullable = true)
    private String type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable=true)
    private Game game;

    @ManyToOne(optional= true,fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id",nullable=true)
    private Player player; 

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable=true)
    private User user;

    public Event() {
    }

    public Event(String color, String type, User user, Game game, Player player) {
        this.valid = false;
        this.type = type;
        this.game = game;
        this.color = color;
        this.user = user;
        this.time = new Timestamp(System.currentTimeMillis());
        this.valid = true;
        this.player.setName("None");
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isValid() {
        return this.valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public Timestamp getTime() {
        return this.time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public String getColor() {
        return this.color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "{" +
            " id='" + getId() + "'" +
            ", valid='" + isValid() + "'" +
            ", time='" + getTime() + "'" +
            ", color='" + getColor() + "'" +
            ", type='" + getType() + "'" +
            "}";
    }

    public Game getGame() {
        return this.game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Player getPlayer() {
        return this.player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
