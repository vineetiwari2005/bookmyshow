package com.driver.bookMyShow.Services;

import com.driver.bookMyShow.Dtos.RequestDtos.LoginRequestDto;
import com.driver.bookMyShow.Dtos.RequestDtos.SignupRequestDto;
import com.driver.bookMyShow.Dtos.ResponseDtos.AuthResponseDto;
import com.driver.bookMyShow.Exceptions.UserAlreadyExistsWithEmail;
import com.driver.bookMyShow.Models.User;
import com.driver.bookMyShow.Repositories.UserRepository;
import com.driver.bookMyShow.Utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * AuthService - Handles user authentication and registration
 * Provides JWT-based login and signup functionality
 */
@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * Register a new user
     * Creates user account with hashed password
     */
    public AuthResponseDto signup(SignupRequestDto signupRequest) throws UserAlreadyExistsWithEmail {
        // Check if user already exists
        if (userRepository.findByEmailId(signupRequest.getEmail()) != null) {
            throw new UserAlreadyExistsWithEmail();
        }

        // Create new user
        User user = User.builder()
                .name(signupRequest.getName())
                .emailId(signupRequest.getEmail())
                .password(passwordEncoder.encode(signupRequest.getPassword()))
                .mobileNo(signupRequest.getMobileNo())
                .age(signupRequest.getAge())
                .address(signupRequest.getAddress())
                .gender(signupRequest.getGender())
                .role(signupRequest.getRole())
                .isActive(true)
                .walletBalance(0.0)
                .build();

        user = userRepository.save(user);

        // Generate tokens
        String accessToken = jwtUtil.generateToken(user.getEmailId(), user.getRole().name());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmailId());

        return AuthResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(10 * 60 * 60L) // 10 hours in seconds
                .email(user.getEmailId())
                .name(user.getName())
                .role(user.getRole().name())
                .userId(user.getId())
                .build();
    }

    /**
     * Login existing user
     * Validates credentials and returns JWT tokens
     */
    public AuthResponseDto login(LoginRequestDto loginRequest) {
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            // Get user details
            User user = userRepository.findByEmailId(loginRequest.getEmail());
            if (user == null) {
                throw new UsernameNotFoundException("User not found");
            }

            // Generate tokens
            String accessToken = jwtUtil.generateToken(user.getEmailId(), user.getRole().name());
            String refreshToken = jwtUtil.generateRefreshToken(user.getEmailId());

            return AuthResponseDto.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .expiresIn(10 * 60 * 60L) // 10 hours in seconds
                    .email(user.getEmailId())
                    .name(user.getName())
                    .role(user.getRole().name())
                    .userId(user.getId())
                    .build();

        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid email or password");
        }
    }

    /**
     * Refresh access token using refresh token
     */
    public AuthResponseDto refreshToken(String refreshToken) {
        try {
            String email = jwtUtil.extractUsername(refreshToken);
            User user = userRepository.findByEmailId(email);

            if (user == null) {
                throw new UsernameNotFoundException("User not found");
            }

            if (jwtUtil.validateToken(refreshToken, email)) {
                String newAccessToken = jwtUtil.generateToken(email, user.getRole().name());

                return AuthResponseDto.builder()
                        .accessToken(newAccessToken)
                        .refreshToken(refreshToken)
                        .tokenType("Bearer")
                        .expiresIn(10 * 60 * 60L)
                        .email(user.getEmailId())
                        .name(user.getName())
                        .role(user.getRole().name())
                        .userId(user.getId())
                        .build();
            } else {
                throw new BadCredentialsException("Invalid refresh token");
            }
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid refresh token");
        }
    }
}
