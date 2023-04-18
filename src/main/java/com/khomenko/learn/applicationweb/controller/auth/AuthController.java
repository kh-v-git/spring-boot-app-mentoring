/*
 * Do not reproduce without permission in writing.
 * Copyright (c) 2023.
 */
package com.khomenko.learn.applicationweb.controller.auth;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.khomenko.learn.applicationweb.controller.auth.request.LoginRequest;
import com.khomenko.learn.applicationweb.controller.auth.request.SignupRequest;
import com.khomenko.learn.applicationweb.controller.auth.response.MessageResponse;
import com.khomenko.learn.applicationweb.controller.auth.response.UserInfoResponse;
import com.khomenko.learn.applicationweb.domain.user.UserEntity;
import com.khomenko.learn.applicationweb.domain.user.UserRoleEntity;
import com.khomenko.learn.applicationweb.domain.user.UserRolesEnum;
import com.khomenko.learn.applicationweb.repository.UserRepository;
import com.khomenko.learn.applicationweb.repository.UserRoleRepository;
import com.khomenko.learn.applicationweb.security.jwt.JwtUtils;
import com.khomenko.learn.applicationweb.security.service.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    private final UserRoleRepository roleRepository;

    private final PasswordEncoder encoder;

    private final JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getName(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

        List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());

        return ResponseEntity.ok()
                             .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                             .body(new UserInfoResponse(userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), jwtCookie.toString(), roles));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByName(signUpRequest.getName())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        UserEntity user =
                UserEntity.builder().name(signUpRequest.getName()).email(signUpRequest.getEmail()).password(encoder.encode(signUpRequest.getPassword())).build();

        Set<String> strRoles = signUpRequest.getRole();
        Set<UserRoleEntity> roles = new HashSet<>();

        if (strRoles == null) {
            UserRoleEntity userRole = roleRepository.findByRole(UserRolesEnum.ROLE_USER).orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        UserRoleEntity adminRole =
                                roleRepository.findByRole(UserRolesEnum.ROLE_ADMIN).orElseThrow(() -> new RuntimeException("Error: Admin Role is not found."));
                        roles.add(adminRole);
                        break;
                    case "user":
                        UserRoleEntity userRole =
                                roleRepository.findByRole(UserRolesEnum.ROLE_USER).orElseThrow(() -> new RuntimeException("Error: User Role is not found."));
                        roles.add(userRole);
                        break;
                    default:
                        UserRoleEntity guestRole =
                                roleRepository.findByRole(UserRolesEnum.ROLE_GUEST).orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(guestRole);
                }
            });
        }

        user.setUserRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser() {
        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(new MessageResponse("You've been signed out!"));
    }

    @GetMapping("/user")
    public Map<String, Object> user(@AuthenticationPrincipal OAuth2User principal) {

        return Collections.singletonMap("name", principal.getAttribute("name"));
    }
}
