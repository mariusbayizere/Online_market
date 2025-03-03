package com.example.Project_Online_market.SecurityConfig;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.crypto.SecretKey;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;


public class JwtProvider {
    static SecretKey key = Keys.hmacShaKeyFor(JwtConstant.SECRET_KEY.getBytes());

    // public static String generateToken(Authentication auth) {
    //     Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
    //     String roles = populateAuthorities(authorities);
    //     String jwt = Jwts.builder()
    //             .setIssuedAt(new Date())
    //             // .setExpiration(new Date(new Date().getTime() + 86400000))
    //             .setExpiration(new Date(new Date().getTime() + 180000))
    //             .claim("email", auth.getName())
    //             .claim("authorities", roles)
    //             .signWith(key)
    //             .compact();

    //     System.out.println("Token for parsing in JwtProvider: " + jwt);
    //     return jwt;
    // }

    public static String generateToken(Authentication auth) {
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        String roles = populateAuthorities(authorities); // This is where roles are populated
    
        // Debugging: print out the roles for verification
        System.out.println("Roles in Token: " + roles);
    
        String jwt = Jwts.builder()
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + 1200000)) // Expiration time in ms
                .claim("email", auth.getName())
                .claim("roles", roles) // Add the role(s) here
                .signWith(key)
                .compact();
    
        System.out.println("Token for parsing in JwtProvider: " + jwt);
        return jwt;
    }
    

    private static String populateAuthorities(Collection<? extends GrantedAuthority> authorities) {
        Set<String> auths = new HashSet<>();
        for (GrantedAuthority authority : authorities) {
            auths.add(authority.getAuthority());
        }
        return String.join(",", auths);
    }

    public static String getEmailFromJwtToken(String jwt) {
        jwt = jwt.substring(7); // Assuming "Bearer " is removed from the token
        try {
                JwtParser parser = Jwts.parserBuilder().setSigningKey(key).build(); // Create parser
                Claims claims = parser.parseClaimsJws(jwt).getBody(); // Parse the token
            String email = String.valueOf(claims.get("email"));
            System.out.println("Email extracted from JWT: " + email);
            return email;
        } catch (Exception e) {
            System.err.println("Error extracting email from JWT: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}

