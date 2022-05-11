package com.sdProj.demo;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import java.util.ArrayList;    
import org.springframework.beans.factory.annotation.Autowired;    
import org.springframework.stereotype.Service;

import com.sdProj.data.Team;

@Service    
public class TeamService   
{    
    @Autowired    
    private TeamRepository teamRepository;

    public List<Team> getAllTeams()  
    {    
        List<Team>userRecords = new ArrayList<>();    
        teamRepository.findAll().forEach(userRecords::add);    
        return userRecords;    
    }

    public void addTeam(Team Team)  
    {
        System.out.println(Team);
        teamRepository.save(Team);    
    }

    public Team getTeamByName(String name) {
        return teamRepository.findByName(name);
    }
/*
    @Transactional
    public void changeTeamOffice(int id, String newoffice) {
        Optional<Team> p = teamRepository.findById(id);
        if (!p.isEmpty())
            p.get().setOffice(newoffice);
    }
*/
}    