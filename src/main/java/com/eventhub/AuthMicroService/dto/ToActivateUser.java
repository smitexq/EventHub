package com.eventhub.AuthMicroService.dto;

import java.util.UUID;

public record ToActivateUser(UUID id, String activation_code) {
}
