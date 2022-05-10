package com.sdProj.demo;

import org.springframework.data.repository.CrudRepository;

import com.sdProj.data.Event;

public interface EventRepository extends CrudRepository<Event, Integer>   
{ } 