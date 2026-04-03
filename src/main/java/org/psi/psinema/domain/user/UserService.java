package org.psi.psinema.domain.user;

import lombok.RequiredArgsConstructor;
import org.psi.psinema.domain.user.dto.AuthResponse;
import org.psi.psinema.domain.user.dto.LoginRequest;
import org.psi.psinema.domain.user.dto.RegisterRequest;
import org.psi.psinema.exception.ConflictException;
import org.psi.psinema.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already registered");
        }
        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .role(Role.CUSTOMER)
                .build();
        userRepository.save(user);
        UserDetails details = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtTokenProvider.generateToken(details);
        return new AuthResponse(token, user.getEmail(), user.getRole().name(), user.getFirstName(), user.getLastName());
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        UserDetails details = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtTokenProvider.generateToken(details);
        return new AuthResponse(token, user.getEmail(), user.getRole().name(), user.getFirstName(), user.getLastName());
    }
}
