package com.sdProj.demo;

import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.sdProj.data.User;

public interface UserRepository extends CrudRepository<User, String>   {

    User findByUsername(String username);

    @Query(value = "select * from users where username = ?1 and password = ?2", nativeQuery = true)
    Optional<User> auth(String username, String password);

    @Query(value = "select role from users where username = ?1", nativeQuery = true)
    Integer getRole(String username);
    
    
    @Modifying
    @org.springframework.transaction.annotation.Transactional
    @Query(value = "update users set role = ?2 where username = ?1", nativeQuery = true)
    void setRole(String username, int role);
 } 