package com.sdProj.demo;

import org.springframework.data.repository.CrudRepository;

import com.sdProj.data.Player;

public interface PlayerRepository extends CrudRepository<Player, String>   
{ } 