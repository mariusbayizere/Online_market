package com.example.Project_Online_market.Controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Project_Online_market.Model.EmailDetails;
import com.example.Project_Online_market.Model.Users;
import com.example.Project_Online_market.Repository.UsersRopository;
import com.example.Project_Online_market.SecurityConfig.JwtProvider;
import com.example.Project_Online_market.response.AuthResponse;
import com.example.Project_Online_market.service.EmailService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
// @RequestMapping("/auth")

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*")
@Tag(name = "User Authentication", description = "APIs for user authentication and registration")
public class UserAutho {

    @Autowired
    private UsersRopository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Operation(summary = "Register a new user", description = "Allows a new user to register in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User registered successfully"),
        @ApiResponse(responseCode = "400", description = "Email already exists or passwords do not match")
    })
    @CrossOrigin("*")
    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> createUserHandler(@RequestBody Users user) {
        String email = user.getEmail();
        Optional<Users> isEmailExist = userRepository.findByEmail(email);
        if (isEmailExist.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthResponse(false, "Email is already used", null));
        }

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        if (!passwordEncoder.matches(user.getConfirmPassword(), encodedPassword)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthResponse(false, "Passwords do not match", null));
        }

        Users savedUser = userRepository.save(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(email, user.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = JwtProvider.generateToken(authentication);

        EmailDetails emailDetails = new EmailDetails();
        emailDetails.setRecipient(email);
        emailDetails.setSubject("Registration Successful");
        emailDetails.setMsgBody("You have successfully registered.");
        emailService.sendSimpleMail(emailDetails);

        return ResponseEntity.ok(new AuthResponse(true, "Register Success", token));
    }

    @Operation(summary = "User Sign-in", description = "Authenticates a user and returns a JWT token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Signin successful"),
        @ApiResponse(responseCode = "400", description = "Invalid username or password")
    })
    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> signin(@RequestBody Users loginRequest) {
        String username = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        Authentication authentication = authenticate(username, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = JwtProvider.generateToken(authentication);

        return ResponseEntity.ok(new AuthResponse(true, "Signin Success", token));
    }

    private Authentication authenticate(String username, String password) {
        Optional<Users> user = userRepository.findByEmail(username);
        if (user.isPresent()) {
            List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(user.get().getUserRole().name());
            return new UsernamePasswordAuthenticationToken(username, password, authorities);
        } else {
            throw new BadCredentialsException("Invalid username or password");
        }
    }
}
