/*
 * Do not reproduce without permission in writing.
 * Copyright (c) 2023.
 */
package com.khomenko.learn.applicationweb.security;

import com.khomenko.learn.applicationweb.security.jwt.AuthEntryPointJwt;
import com.khomenko.learn.applicationweb.security.jwt.AuthTokenFilter;
import com.khomenko.learn.applicationweb.security.oauth.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfiguration {
    private static final String[] AUTH_WHITELIST = { "/", "/login", "/favicon.ico", "/index*", "/error", "/webjars/**", "/api/test/all/**", "/api/parking-lot/**"};

    private final AuthEntryPointJwt unauthorizedHandler;
    private final DaoAuthenticationProvider authenticationProvider;
    private final AuthTokenFilter authenticationJwtTokenFilter;
    private final CustomOAuth2UserService userService;

    @Value("${spring.h2.console.path}")
    private String h2ConsolePath;

    @Bean
    public SecurityFilterChain actuatorSecurityFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher(EndpointRequest.toAnyEndpoint());
        http.authorizeHttpRequests((requests) -> requests.anyRequest().permitAll());
        http.httpBasic(withDefaults());
        return http.build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))

            .exceptionHandling(authHandler -> authHandler.authenticationEntryPoint(unauthorizedHandler))

            .authorizeHttpRequests(authorize -> {

                authorize.requestMatchers(AUTH_WHITELIST).permitAll();

                authorize.requestMatchers(HttpMethod.POST, "/api/auth/signin").permitAll();
                authorize.requestMatchers(HttpMethod.POST, "/api/auth/signup").permitAll();
                authorize.requestMatchers(HttpMethod.POST, "/api/auth/signout").permitAll();

                authorize.requestMatchers(HttpMethod.GET, "/api/test/all/guest").hasAnyRole("USER", "GUEST", "ADMIN");
                authorize.requestMatchers(HttpMethod.GET, "/api/test/all/user").hasAnyRole("USER", "ADMIN");
                authorize.requestMatchers(HttpMethod.GET, "/api/test/all/admin").hasAnyRole("ADMIN");

                authorize.anyRequest().authenticated();
            })

            .cors(AbstractHttpConfigurer::disable)
//            .csrf(c -> c.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                .csrf(AbstractHttpConfigurer::disable)

            .oauth2Login(l->l.userInfoEndpoint(u  -> u.userService(userService)))
                .logout(l -> l.logoutSuccessUrl("/").permitAll())

            .authenticationProvider(authenticationProvider)
            .addFilterBefore(authenticationJwtTokenFilter, UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }
}
