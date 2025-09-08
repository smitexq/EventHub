package com.eventhub.AuthMicroService.dto.to_profile;

import java.util.UUID;

public class NewProfile {
    private UUID id;
    private String username;
    private String email;
    private int age;


    public NewProfile(UUID id, String username, String email, int age) {
        this.age = age;
        this.username = username;
        this.email = email;
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public int getAge() {
        return age;
    }
}
