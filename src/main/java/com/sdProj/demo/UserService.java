package com.sdProj.demo;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import java.util.ArrayList;    
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.sdProj.data.User;

@Service    
public class UserService implements UserDetailsService {
   
    @Autowired    
    private UserRepository userRepository;

    public List<User> getAllUsers()  
    {    
        List<User>userRecords = new ArrayList<>();    
        userRepository.findAll().forEach(userRecords::add);    
        return userRecords;    
    }

    public void addUser(User User)  
    {
        System.out.println(User);
        userRepository.save(User);    
    }
    
/*
    public Optional<User> getUser(int id) {
        return userRepository.findById(id);
    }

    @Transactional
    public void changeUserOffice(int id, String newoffice) {
        Optional<User> p = userRepository.findById(id);
        if (!p.isEmpty())
            p.get().setOffice(newoffice);
    }
*/

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if(user == null){
            throw new UsernameNotFoundException("User Not Found");
        }
        //return new CustomUserDetails(user);
        return null;
    }
}    

