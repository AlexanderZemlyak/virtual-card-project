package com.example.user_service.domain;

import com.example.user_service.api.dto.UserResponse;
import com.example.user_service.db.entities.User;
import com.example.user_service.db.UserRepository;
import com.example.user_service.domain.exceptions.UserNameIsAlreadyTaken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository repository;
    private final PasswordEncoder encrypter;
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public UserService(UserRepository repository,
                       AuthenticationConfiguration configuration,
                       PasswordEncoder encrypter) {
        this.repository = repository;
        this.authenticationManager = configuration.getAuthenticationManager();
        this.encrypter = encrypter;
    }

    public Authentication authenticateUser(String userName, String password) {
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userName,
                        password
                )
        );
    }

    @Transactional
    public User createUser(UserData userData) {
        if (repository.findByName(userData.userName()).isPresent())
            throw new UserNameIsAlreadyTaken("Имя " + userData.userName() + " уже занято.");

        log.info("Saving user with name {}...", userData.userName());

        return repository.save(new User(
                userData.userName(),
                encrypter.encode(userData.password()),
                userData.age()
        ));
    }

    @Transactional(readOnly = true)
    public UserResponse getUser(UUID id) {

        User customer = repository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Пользователь с таким id не найден"
                        )
                );

        return new UserResponse(
                customer.getId(),
                customer.getName(),
                customer.getAge()
        );
    }
}
