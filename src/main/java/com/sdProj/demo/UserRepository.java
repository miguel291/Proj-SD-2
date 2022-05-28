package com.sdProj.demo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.sdProj.data.User;

public interface UserRepository extends CrudRepository<User, String>   {

    User findByUsername(String username);
    
    @Query(value = "INSERT INTO user(phone, name, username, password, role) VALUES (%?1,%?2,%?3,%?4,%?5)", nativeQuery = true)
    public int addUser(long phone, String name, String username, String password, String role);
 } 