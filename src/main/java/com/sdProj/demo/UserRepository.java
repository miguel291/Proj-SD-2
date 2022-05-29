package com.sdProj.demo;

import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.sdProj.data.User;

public interface UserRepository extends CrudRepository<User, String>   {

    User findByUsername(String username);

    @Query(value = "select * from users where username = ?1 and password = ?2", nativeQuery = true)
    Optional<User> auth(String username, String password);
 } 