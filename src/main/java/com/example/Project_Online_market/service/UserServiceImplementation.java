package com.example.Project_Online_market.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.Project_Online_market.Model.Users;
import com.example.Project_Online_market.Repository.UsersRopository;

@Service
public class UserServiceImplementation implements UserDetailsService {

    @Autowired
    private UsersRopository userRepository;

    @SuppressWarnings("unused")
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Users user = userRepository.findByEmail(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
        System.out.println(user);

        if (user == null) {
            throw new UsernameNotFoundException("User not found with this email: " + username);
        }

        System.out.println("Loaded user: " + user.getEmail() + ", Role: " + user.getUserRole());
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getUserRole().toString())); // Add user role to authorities

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                authorities);
    }
}