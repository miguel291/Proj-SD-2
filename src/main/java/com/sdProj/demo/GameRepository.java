package com.sdProj.demo;

import org.springframework.data.repository.CrudRepository;

import com.sdProj.data.Game;

public interface GameRepository extends CrudRepository<Game, Integer>   
{ } 