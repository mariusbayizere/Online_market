package com.example.Project_Online_market.service;

import java.util.List;

import com.example.Project_Online_market.Model.Users;

public interface UserService {
    
    public List<Users> getAllUser();

    public Users findUserProfileByJwt(String jwt);

    public Users findUserByEmail(String email);

    public Users findUserById(String userId);

    public List<Users> findAllUsers();
}
