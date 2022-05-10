package com.sdProj.data;

import java.sql.Time;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class Event {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private boolean valid;
    private Time time;
}
