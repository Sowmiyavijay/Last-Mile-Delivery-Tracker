package com.lastmile.tracker.service;

import com.lastmile.tracker.dto.AuthResponse;
import com.lastmile.tracker.dto.LoginRequest;
import com.lastmile.tracker.dto.RegisterRequest;
import com.lastmile.tracker.dto.UserResponse;
import com.lastmile.tracker.entity.User;
import com.lastmile.tracker.enums.Role;
import com.lastmile.tracker.exception.DuplicateEmailException;
import com.lastmile.tracker.repository.UserRepository;
import com.lastmile.tracker.security.CustomUserDetailsService;
import com.lastmile.tracker.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final UserService userService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException("Email is already registered");
        }

        Role role = request.getRole() != null ? request.getRole() : Role.CUSTOMER;

        // Public registration is limited to CUSTOMER role only
        if (role != Role.CUSTOMER) {
            throw new IllegalArgumentException("Registration is only allowed for CUSTOMER role");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.CUSTOMER)
                .build();

        User savedUser = userRepository.save(user);
        UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.getEmail());
        String token = jwtService.generateToken(userDetails);

        return AuthResponse.builder()
                .token(token)
                .user(userService.toUserResponse(savedUser))
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userService.findByEmail(request.getEmail());
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtService.generateToken(userDetails);

        return AuthResponse.builder()
                .token(token)
                .user(userService.toUserResponse(user))
                .build();
    }

    public UserResponse getCurrentUser(String email) {
        User user = userService.findByEmail(email);
        return userService.toUserResponse(user);
    }
}
