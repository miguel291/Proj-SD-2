package com.sdProj.demo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import com.sdProj.data.Team;

public interface TeamRepository extends CrudRepository<Team, String>   {
    @Query("select s from Team s where s.name like %?1")
    public Team findByName(String name); 
} 