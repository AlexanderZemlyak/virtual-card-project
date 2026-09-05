package com.example.user_service.db.entities;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "users")
public class User {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID id;

    @Column(name = "name", unique = true, nullable = false, length = 50)
    public String name;

    @Column(name = "password_hash", nullable = false, length = 100)
    public String passwordHash;

    @Column(name = "age", nullable = false)
    public Integer age;

    public User() {}

    public User(String name, String passwordHash, Integer age) {
        this.name = name;
        this.passwordHash = passwordHash;
        this.age = age;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Integer getAge() { return age; }
}
