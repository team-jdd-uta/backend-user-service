package com.teamuta.userinfoserver.service;

import com.teamuta.userinfoserver.dto.UserRegisteredEvent;
import com.teamuta.userinfoserver.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class UserRegistrationProjectionService {

    private static final Logger log = LoggerFactory.getLogger(UserRegistrationProjectionService.class);
    private static final int SUPPORTED_EVENT_VERSION = 1;

    private final CustomerRepository customerRepository;

    public UserRegistrationProjectionService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional
    public void project(UserRegisteredEvent event) {
        validate(event);

        int inserted = customerRepository.insertConsumedUserEvent(event.eventId());
        if (inserted == 0) {
            log.info("Skipping duplicate user registration eventId={} userId={}", event.eventId(), event.userId());
            return;
        }

        LocalDateTime createdAt = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(event.occurredAt()),
                ZoneOffset.UTC
        );

        customerRepository.upsertRegisteredUser(event.userId(), event.name(), event.email(), createdAt);
    }

    private void validate(UserRegisteredEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("UserRegisteredEvent must not be null");
        }
        if (event.eventId() == null || event.eventId().isBlank()) {
            throw new IllegalArgumentException("eventId must not be blank");
        }
        if (event.userId() == null || event.userId().isBlank()) {
            throw new IllegalArgumentException("userId must not be blank");
        }
        if (event.name() == null || event.name().isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        if (event.email() == null || event.email().isBlank()) {
            throw new IllegalArgumentException("email must not be blank");
        }
        if (event.occurredAt() <= 0) {
            throw new IllegalArgumentException("occurredAt must be epoch millis");
        }
        if (event.eventVersion() != SUPPORTED_EVENT_VERSION) {
            throw new IllegalArgumentException("Unsupported user registered eventVersion=" + event.eventVersion());
        }
    }
}
