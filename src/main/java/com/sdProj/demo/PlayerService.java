package com.sdProj.demo;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import java.util.ArrayList;    
import org.springframework.beans.factory.annotation.Autowired;    
import org.springframework.stereotype.Service;

import com.sdProj.data.Player;

@Service    
public class PlayerService   
{    
    @Autowired    
    private PlayerRepository playerRepository;

    public List<Player> getAllPlayers()  
    {    
        List<Player>userRecords = new ArrayList<>();    
        playerRepository.findAll().forEach(userRecords::add);    
        return userRecords;    
    }

    public void addPlayer(Player player)  
    {
        System.out.println(player);
        playerRepository.save(player);    
    }
/*
    public Optional<Player> getPlayer(int id) {
        return playerRepository.findById(id);
    }

    @Transactional
    public void changeplayerOffice(int id, String newoffice) {
        Optional<Player> p = playerRepository.findById(id);
        if (!p.isEmpty())
            p.get().setOffice(newoffice);
    }
*/
}    