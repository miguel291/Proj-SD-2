package com.sdProj.demo;

import org.springframework.data.repository.CrudRepository;

import com.sdProj.data.Professor;

public interface ProfRepository extends CrudRepository<Professor, Integer>   
{ } 