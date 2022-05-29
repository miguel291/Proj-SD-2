package com.sdProj.demo;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import java.util.ArrayList;    
import org.springframework.beans.factory.annotation.Autowired;    
import org.springframework.stereotype.Service;

import net.bytebuddy.asm.Advice.Return;

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
        playerRepository.save(player);    
    }
  
   public Optional<Player> getPlayer(String id) {
        return playerRepository.findById(id);
    }
  
   public Player addPlayerNull()  
    {
        Player player = new Player();
        player.setName("None");  
        return player;
    }
/*
    @Transactional
    public void changeplayerOffice(int id, String newoffice) {
        Optional<Player> p = playerRepository.findById(id);
        if (!p.isEmpty())
            p.get().setOffice(newoffice);
    }
*/
}    