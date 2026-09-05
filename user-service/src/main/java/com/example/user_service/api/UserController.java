package com.example.user_service.api;

import com.example.user_service.api.dto.*;
import com.example.user_service.db.entities.User;
import com.example.user_service.domain.JwtService;
import com.example.user_service.domain.UserData;
import com.example.user_service.domain.UserService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    public UserController(UserService userService,
                          JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {

        User createdUser = userService.createUser(new UserData(registerRequest.userName(),
                registerRequest.password(), registerRequest.age()));

        var authentication = userService.authenticateUser(registerRequest.userName(),
                registerRequest.password());

        log.info("User {} was authenticated (registration)", registerRequest.userName());

        return ResponseEntity.status(HttpStatus.CREATED).body(new RegisterResponse(
                createdUser.getId().toString(),
                new TokenResponse(
                jwtService.generateToken(authentication)
        )));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        var authentication = userService.authenticateUser(loginRequest.userName(),
                loginRequest.password());

        log.info("User {} was authenticated (login)", loginRequest.userName());

        return ResponseEntity.ok(new TokenResponse(
                jwtService.generateToken(authentication)
        ));
    }

    @GetMapping("/{id}")
    public UserResponse getCustomer(
            @NotNull @PathVariable UUID id
    ) {
        return userService.getUser(id);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        // Тут инвалидировать Refresh Token
        return ResponseEntity.noContent().build();
    }
}
