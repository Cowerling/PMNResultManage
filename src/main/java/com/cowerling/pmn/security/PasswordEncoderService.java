package com.cowerling.pmn.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordEncoderService {
    private final PasswordEncoder passwordEncoder;

    public PasswordEncoderService() {
        passwordEncoder = new BCryptPasswordEncoder();
    }

    public PasswordEncoder getEncoder() {
        return passwordEncoder;
    }

    public String encode(String password) {
        return passwordEncoder.encode(password);
    }
}
