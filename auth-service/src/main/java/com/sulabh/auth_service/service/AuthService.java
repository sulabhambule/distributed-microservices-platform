package com.sulabh.auth_service.service;

import com.sulabh.auth_service.dto.LoginRequestDTO;
import com.sulabh.auth_service.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    public AuthService(PasswordEncoder passwordEncoder, UserService userService, JwtUtil jwtUtil) {
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    public Optional<String> authenticate(LoginRequestDTO loginRequestDTO) {
        // we are receiving the remail and the password from the user.
        // first find the user from the database by using email then verify
        // there hashed password with input password and then generate the token.

        // userService.findByEmail(loginRequestDTO.getEmail()) - this returns the User Object

        Optional<String> token = userService.findByEmail(loginRequestDTO.getEmail())
                .filter(u -> passwordEncoder.matches(
                        loginRequestDTO.getPassword(),
                        u.getPassword()
                ))
                .map(u -> jwtUtil.generateToken(u.getEmail(), u.getRole()));

        // filter takes the user and if password matches it returns true so filter keeps
        // the user in Optional datatype , if password is wrong in that case it returns false and
        // removes the user so chain has stopped. and if  the filter passed then map take the
        // user object and convert it into a string (token) (transform object into another object)

        return token;
    }

    public boolean validateToken(String token) {
        try{
            jwtUtil.validateToken(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}
