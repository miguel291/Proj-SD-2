package com.sdProj.demo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.sdProj.data.Professor;

public interface ProfRepository extends CrudRepository<Professor, Integer>   
{
    @Query("select p from Professor p where p.name = ?1")
    public Professor findByName(String name);
 } 