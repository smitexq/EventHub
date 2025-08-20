package com.eventhub.AuthMicroService.dto.to_profile;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class NewProfile {
    private UUID id;
    private String username;
    private int age;


    public NewProfile(UUID id, String username, int age) {
        this.age = age;
        this.username = username;
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public int getAge() {
        return age;
    }
}
