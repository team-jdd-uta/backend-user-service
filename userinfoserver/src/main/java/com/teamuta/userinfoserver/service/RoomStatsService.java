package com.teamuta.userinfoserver.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class RoomStatsService {

    private final RestClient restClient;

    public RoomStatsService(@Value("${room.service.base-url:http://localhost:8082}") String roomServiceBaseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(trimTrailingSlash(roomServiceBaseUrl))
                .build();
    }

    public int countStreamsByBroadcaster(String userId) {
        if (userId == null || userId.isBlank()) {
            return 0;
        }

        try {
            List<Map<String, Object>> rooms = restClient.get()
                    .uri("/rooms")
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});

            if (rooms == null || rooms.isEmpty()) {
                return 0;
            }

            return (int) rooms.stream()
                    .filter(room -> userId.equals(String.valueOf(room.getOrDefault("broadcasterId", ""))))
                    .count();
        } catch (Exception error) {
            log.warn("Failed to load stream count from room-service. userId={}", userId, error);
            return 0;
        }
    }

    private static String trimTrailingSlash(String value) {
        if (value == null || value.isBlank()) {
            return "http://localhost:8082";
        }
        return value.replaceAll("/+$", "");
    }
}
