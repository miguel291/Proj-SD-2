package com.sdProj.demo;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import java.util.ArrayList;    
import org.springframework.beans.factory.annotation.Autowired;    
import org.springframework.stereotype.Service;

import com.sdProj.data.Game;
import com.sdProj.data.Team;

@Service    
public class GameService   
{    
    @Autowired    
    private GameRepository gameRepository;

    public List<Game> getAllGames()  
    {    
        List<Game>userRecords = new ArrayList<>();    
        gameRepository.findAll().forEach(userRecords::add);    
        return userRecords;    
    }

    public void addGame(Game Game)  
    {
        //System.out.println(Game);
        gameRepository.save(Game);    
    }

    public Optional<Game> getGame(int id) {
        return gameRepository.findById(id);
    }

    public List<Integer> getGamesIds(Team t1, Team t2) {
        return gameRepository.getGamesIds(t1.getName(), t2.getName());
    }

    public List<List<Object>> getGoalsAndLocation(Team t1, Team t2) {
        return gameRepository.getGoalsAndLocation(t1.getName(), t2.getName());
    }

    public List<List<Object>> getCurrentGames() {
        return gameRepository.getCurrentGames();
    }
    public List<Integer> getGames(){
        return  gameRepository.getGames();
    }
/*
    @Transactional
    public void changeGameOffice(int id, String newoffice) {
        Optional<Game> p = gameRepository.findById(id);
        if (!p.isEmpty())
            p.get().setOffice(newoffice);
    }
*/
}    