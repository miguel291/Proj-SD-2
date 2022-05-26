package com.sdProj.demo;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import java.util.ArrayList;    
import org.springframework.beans.factory.annotation.Autowired;    
import org.springframework.stereotype.Service;

import com.sdProj.data.Event;
import com.sdProj.data.Team;

@Service    
public class EventService   
{    
    @Autowired    
    private EventRepository eventRepository;

    public List<Event> getAllEvents()  
    {    
        List<Event>userRecords = new ArrayList<>();    
        eventRepository.findAll().forEach(userRecords::add);    
        return userRecords;    
    }

    public void addEvent(Event prof)  
    {
        System.out.println(prof);
        eventRepository.save(prof);    
    }

    public Optional<Event> getEvent(int id) {
        return eventRepository.findById(id);
    }

    public List<Object[]> getMaxGoalsInGame() {
        return eventRepository.maxGoalsInGame();
    }

    public List<Object[]> getGoalsStatsPerPlayer() {
        return eventRepository.goalsStatsPerPlayer();
    }

    public List<Integer> getCountCards(Team t1, Team t2, String cardColor) {
        System.out.println(eventRepository.getTeamCards(t1.getName(), t2.getName(), cardColor));
        return eventRepository.getTeamCards(t1.getName(), t2.getName(), cardColor);
    }

    public List<List<Object>> getEventsByGameId(int id){
        return eventRepository.getEventsByGameId(id);
    }
/*
    @Transactional
    public void changeProfOffice(int id, String newoffice) {
        Optional<Event> p = eventRepository.findById(id);
        if (!p.isEmpty())
            p.get().setOffice(newoffice);
    }*/

}    