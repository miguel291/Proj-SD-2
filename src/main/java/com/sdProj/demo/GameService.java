package com.sdProj.demo;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import java.util.ArrayList;    
import org.springframework.beans.factory.annotation.Autowired;    
import org.springframework.stereotype.Service;

import com.sdProj.data.Game;

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
        System.out.println(Game);
        gameRepository.save(Game);    
    }

    public Optional<Game> getGame(int id) {
        return gameRepository.findById(id);
    }

    @Transactional
    public void changeGameOffice(int id, String newoffice) {
        Optional<Game> p = gameRepository.findById(id);
        if (!p.isEmpty())
            p.get().setOffice(newoffice);
    }

}    