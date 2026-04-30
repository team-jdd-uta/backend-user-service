package com.teamuta.userinfoserver.messaging;

import com.teamuta.userinfoserver.dto.UserRegisteredEvent;
import com.teamuta.userinfoserver.service.UserRegistrationProjectionService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;

@Component
@ConditionalOnProperty(name = "app.kafka.consumer.enabled", havingValue = "true", matchIfMissing = true)
public class UserRegisteredEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(UserRegisteredEventConsumer.class);
    private static final String USER_REGISTERED = "UserRegistered";
    private static final String USER_AGGREGATE = "user";

    private final ObjectMapper objectMapper;
    private final UserRegistrationProjectionService projectionService;

    public UserRegisteredEventConsumer(ObjectMapper objectMapper, UserRegistrationProjectionService projectionService) {
        this.objectMapper = objectMapper;
        this.projectionService = projectionService;
    }

    @KafkaListener(topics = "${app.kafka.topics.user-registered}")
    public void consume(ConsumerRecord<String, String> record) {
        String eventType = headerValue(record, "event_type");
        if (eventType == null) {
            eventType = headerValue(record, "eventType");
        }

        if (eventType == null) {
            log.warn("Skipping user event without event_type header key={} topic={} partition={} offset={}",
                    record.key(), record.topic(), record.partition(), record.offset());
            return;
        }

        if (!USER_REGISTERED.equals(eventType)) {
            log.debug("Skipping user event type={} key={} topic={} offset={}",
                    eventType, record.key(), record.topic(), record.offset());
            return;
        }

        String aggregateType = headerValue(record, "aggregate_type");
        if (aggregateType == null) {
            aggregateType = headerValue(record, "aggregateType");
        }
        if (aggregateType != null && !USER_AGGREGATE.equals(aggregateType)) {
            log.warn("Skipping non-user aggregate event aggregateType={} key={} topic={} offset={}",
                    aggregateType, record.key(), record.topic(), record.offset());
            return;
        }

        UserRegisteredEvent event;
        try {
            event = objectMapper.readValue(record.value(), UserRegisteredEvent.class);
        } catch (JacksonException e) {
            log.error("Skipping malformed user event key={} topic={} offset={}: {}",
                    record.key(), record.topic(), record.offset(), e.getMessage());
            return;
        }

        try {
            projectionService.project(event);
        } catch (IllegalArgumentException e) {
            log.error("Skipping invalid user event eventId={} userId={}: {}",
                    event.eventId(), event.userId(), e.getMessage());
            return;
        }

        log.info("Projected user registration eventId={} userId={} topic={} partition={} offset={}",
                event.eventId(), event.userId(), record.topic(), record.partition(), record.offset());
    }

    private String headerValue(ConsumerRecord<String, String> record, String name) {
        Header header = record.headers().lastHeader(name);
        if (header == null || header.value() == null) {
            return null;
        }
        return new String(header.value(), StandardCharsets.UTF_8);
    }
}
