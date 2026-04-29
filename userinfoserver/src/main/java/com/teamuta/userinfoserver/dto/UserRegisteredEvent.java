package com.teamuta.userinfoserver.dto;

public record UserRegisteredEvent(
        String eventId,
        String userId,
        String email,
        String name,
        long occurredAt,
        int eventVersion
) {
}
