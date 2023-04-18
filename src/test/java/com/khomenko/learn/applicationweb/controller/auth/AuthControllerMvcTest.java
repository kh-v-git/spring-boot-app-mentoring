/*
 * Do not reproduce without permission in writing.
 * Copyright (c) 2023.
 */
package com.khomenko.learn.applicationweb.controller.auth;

import java.util.Collections;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.khomenko.learn.applicationweb.controller.auth.request.LoginRequest;
import com.khomenko.learn.applicationweb.controller.auth.request.SignupRequest;
import com.khomenko.learn.applicationweb.domain.user.UserEntity;
import com.khomenko.learn.applicationweb.domain.user.UserRoleEntity;
import com.khomenko.learn.applicationweb.domain.user.UserRolesEnum;
import com.khomenko.learn.applicationweb.repository.UserRepository;
import com.khomenko.learn.applicationweb.repository.UserRoleRepository;
import com.khomenko.learn.applicationweb.security.jwt.AuthTokenFilter;
import com.khomenko.learn.applicationweb.security.jwt.JwtUtils;
import com.khomenko.learn.applicationweb.security.service.UserDetailsImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerMvcTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserRoleRepository roleRepository;

    @MockBean
    private PasswordEncoder encoder;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private AuthTokenFilter authTokenFilter;

    @Mock
    private Authentication authentication;

    @Test
    public void shouldAuthenticateUser() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setName("username");
        loginRequest.setPassword("password");

        UserDetailsImpl userDetails =
                new UserDetailsImpl(1L, "username", "email@example.com", "password", Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(userRepository.findByName(loginRequest.getName())).thenReturn(Optional.of(new UserEntity(1L, "username", "email@example.com", "password", null)));
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(encoder.matches(loginRequest.getPassword(), userDetails.getPassword())).thenReturn(true);
        when(jwtUtils.generateJwtCookie(any(UserDetailsImpl.class))).thenReturn(ResponseCookie.from("jwtCookie", "jwtValue").build());

        mockMvc.perform(post("/api/auth/signin").contentType(MediaType.APPLICATION_JSON).content(asJsonString(loginRequest)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.name").value(userDetails.getUsername()))
               .andExpect(jsonPath("$.email").value(userDetails.getEmail()))
               .andExpect(jsonPath("$.roles[0]").value(userDetails.getAuthorities().iterator().next().getAuthority()))
               .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("jwtCookie")));
    }

    @Test
    public void shouldRegisterUser() throws Exception {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setName("username");
        signupRequest.setEmail("email@example.com");
        signupRequest.setPassword("password");

        when(userRepository.existsByName(signupRequest.getName())).thenReturn(false);
        when(userRepository.existsByEmail(signupRequest.getEmail())).thenReturn(false);
        when(roleRepository.findByRole(UserRolesEnum.ROLE_USER)).thenReturn(Optional.of(new UserRoleEntity(1L, UserRolesEnum.ROLE_USER)));

        mockMvc.perform(post("/api/auth/signup").contentType(MediaType.APPLICATION_JSON).content(asJsonString(signupRequest)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.message").value("User registered successfully!"));
    }

    @Test
    @WithMockUser(value = "spring")
    public void shouldLogoutUser() throws Exception {
        when(jwtUtils.getCleanJwtCookie()).thenReturn(ResponseCookie.from("jwtCookie", "").maxAge(0).build());

        mockMvc.perform(post("/api/auth/signout"))
               .andExpect(status().isOk())
               .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("jwtCookie")))
               .andExpect(jsonPath("$.message").value("You've been signed out!"));
    }

    private static String asJsonString(Object obj) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}