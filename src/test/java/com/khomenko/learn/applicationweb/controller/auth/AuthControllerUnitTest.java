package com.khomenko.learn.applicationweb.controller.auth;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import com.khomenko.learn.applicationweb.controller.auth.request.LoginRequest;
import com.khomenko.learn.applicationweb.controller.auth.request.SignupRequest;
import com.khomenko.learn.applicationweb.controller.auth.response.MessageResponse;
import com.khomenko.learn.applicationweb.controller.auth.response.UserInfoResponse;
import com.khomenko.learn.applicationweb.domain.user.UserRoleEntity;
import com.khomenko.learn.applicationweb.domain.user.UserRolesEnum;
import com.khomenko.learn.applicationweb.repository.UserRepository;
import com.khomenko.learn.applicationweb.repository.UserRoleRepository;
import com.khomenko.learn.applicationweb.security.jwt.JwtUtils;
import com.khomenko.learn.applicationweb.security.service.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerUnitTest {

    @InjectMocks
    AuthController authController;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserRoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserDetailsImpl userDetails;

    @Mock
    private Authentication authentication;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private MockHttpServletResponse mockResponse;

    @BeforeEach
    public void setUp() {
        mockResponse = new MockHttpServletResponse();
    }

    @Test
    public void shouldAuthenticateUserTest() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setName("test");
        loginRequest.setPassword("test");

        SecurityContextHolder.getContext().setAuthentication(authentication);
        given(authenticationManager.authenticate(any())).willReturn(authentication);
        given(authentication.getPrincipal()).willReturn(userDetails);
        given(userDetails.getId()).willReturn(1L);
        given(userDetails.getUsername()).willReturn("test");
        given(userDetails.getEmail()).willReturn("test@test.com");
        Collection grantedAuthorities = Lists.newArrayList(new SimpleGrantedAuthority("ROLE_USER"));
        when(userDetails.getAuthorities()).thenReturn(grantedAuthorities);

        given(jwtUtils.generateJwtCookie(any())).willReturn(ResponseCookie.from("jwtCookie", "jwtValue").build());

        // Act
        ResponseEntity<?> responseEntity = authController.authenticateUser(loginRequest);

        // Assert
        UserInfoResponse userInfoResponse = (UserInfoResponse) responseEntity.getBody();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(1L, userInfoResponse.getId().longValue());
        assertEquals("test", userInfoResponse.getName());
        assertEquals("test@test.com", userInfoResponse.getEmail());
        assertEquals("jwtCookie=jwtValue", responseEntity.getHeaders().getFirst(HttpHeaders.SET_COOKIE));
        assertEquals(Collections.singletonList("ROLE_USER"), userInfoResponse.getRoles());
    }

    @Test
    public void shouldReturnBadRequestWhenUsernameAlreadyTaken() {
        // Arrange
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setName("test");
        signupRequest.setEmail("test@test.com");
        signupRequest.setPassword("password");

        when(userRepository.existsByName(anyString())).thenReturn(true);

        // Act
        ResponseEntity<?> response = authController.registerUser(signupRequest);

        // Assert
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        MessageResponse messageResponses = (MessageResponse) response.getBody();
        assertEquals(messageResponses.getMessage(), "Error: Username is already taken!");
    }

    @Test
    public void shouldReturnBadRequestWhenUserEmailAlreadyTaken() {
        // Arrange
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setName("test");
        signupRequest.setEmail("test@test.com");
        signupRequest.setPassword("password");

        when(userRepository.existsByName(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // Act
        ResponseEntity<?> response = authController.registerUser(signupRequest);

        // Assert
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        MessageResponse messageResponses = (MessageResponse) response.getBody();
        assertEquals(messageResponses.getMessage(), "Error: Email is already in use!");
    }

    @Test
    public void shouldSaveUserAndReturnOkResponseWhenRegisterUser() {
        // Arrange
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setName("test");
        signupRequest.setEmail("test@test.com");
        signupRequest.setPassword("password");

        when(userRepository.existsByName(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(roleRepository.findByRole(UserRolesEnum.ROLE_USER)).thenReturn(Optional.of(new UserRoleEntity(1L, UserRolesEnum.ROLE_USER)));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        // Act
        ResponseEntity<?> response = authController.registerUser(signupRequest);

        // Assert
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        MessageResponse messageResponses = (MessageResponse) response.getBody();
        assertEquals(messageResponses.getMessage(), "User registered successfully!");
    }
    @Test
    public void shouldLogoutUser() {
        // Arrange
        when(jwtUtils.getCleanJwtCookie()).thenReturn(ResponseCookie.from("jwt", "").build());

        // Create an instance of AuthController
        AuthController authController = new AuthController(null, null, null, null, jwtUtils);

        // Act
        ResponseEntity<?> responseEntity = authController.logoutUser();

        // Assert
        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals("You've been signed out!", ((MessageResponse) responseEntity.getBody()).getMessage());
        assertEquals("jwt=", responseEntity.getHeaders().getFirst(HttpHeaders.SET_COOKIE));
    }
}