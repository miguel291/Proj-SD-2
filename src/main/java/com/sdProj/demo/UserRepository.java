package com.sdProj.demo;

import org.springframework.data.repository.CrudRepository;

import com.sdProj.data.User;

public interface UserRepository extends CrudRepository<User, String>   
{ } 