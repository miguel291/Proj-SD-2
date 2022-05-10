package com.sdProj.demo;

import org.springframework.data.repository.CrudRepository;

import com.sdProj.data.Team;

public interface TeamRepository extends CrudRepository<Team, String>   
{ } 