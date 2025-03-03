package com.example.Project_Online_market.service;

import java.util.List;

import com.example.Project_Online_market.Model.Users;


public class Userimplemtation implements UserService {

    @Override
    public List<Users> getAllUser() {
        return List.of();
    }

    @Override
    public Users findUserProfileByJwt(String jwt) {

        return null;
    }

    @Override
    public Users findUserByEmail(String email) {
        return null;

    }

    @Override
    public Users findUserById(String userId) {
        return null;
    }

    @Override
    public List<Users> findAllUsers() {
        return List.of();
    }

}
