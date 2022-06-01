package com.sdProj.demo;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.transaction.Transactional;


import java.sql.Timestamp;
import java.util.ArrayList;    
import org.springframework.beans.factory.annotation.Autowired;    
import org.springframework.stereotype.Service;

import com.sdProj.data.Event;
import com.sdProj.data.Player;
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

    public void addEvent(Event event)  
    {   
        //System.out.println(prof);
        Timestamp time = new Timestamp(System.currentTimeMillis());
        event.setTime(time);
        event.setValid(false);
        eventRepository.save(event);    
        //Função update cartões amarelos: dá erro uma vez que não existem jogos ativos no currentGames
        //eventRepository.insertYellowCard(event.getGame().getId(), event.getPlayer().getName());
        
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

    public List<List<Object>> getCountCards(Team t1, Team t2, String cardColor) {
        //System.out.println(eventRepository.getTeamCards(t1.getName(), t2.getName(), cardColor));
        return eventRepository.getTeamCards(t1.getName(), t2.getName(), cardColor);
    }

    public List<List<Object>> getEventsByGameId(int id){
        return eventRepository.getEventsByGameId(id);
    }

    public List<List<Object>> insertYellowCard(int gameId, String player){
        return eventRepository.insertYellowCard(gameId, player);
    }

    public List<Event> selectFalseEvents(){
        return eventRepository.selectFalseEvents();
    }

    public void validateEvents(int id){
        eventRepository.validateEvents(id);

    }

    /*public List<List<Object>> insertInt(){
        return eventRepository.insertInt();
    }

    public List<List<Object>> insertIntNull(){
        return eventRepository.insertInt();
    }*/

    /*public Event insertYellowCard(String type, boolean valid, int gameId, String playerId, String user){
        return eventRepository.insertYellowCard( type, valid, gameId, playerId, user);
    }*/
/*
    @Transactional
    public void changeProfOffice(int id, String newoffice) {
        Optional<Event> p = eventRepository.findById(id);
        if (!p.isEmpty())
            p.get().setOffice(newoffice);
    }*/

}    