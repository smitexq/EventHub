package com.eventhub.AuthMicroService.models;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String username;
    private String email;
    private String password;
    private int age;


    public User() {}
    public User(String username, String email, int age, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.age = age;
    }



    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getAge() {
        return age;
    }
}
