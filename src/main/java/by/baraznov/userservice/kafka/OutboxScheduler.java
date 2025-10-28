package by.baraznov.userservice.kafka;

import by.baraznov.userservice.dto.user.UserEvent;
import by.baraznov.userservice.model.OutboxEvent;
import by.baraznov.userservice.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OutboxScheduler {

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, UserEvent> kafkaTemplate;

    @Scheduled(fixedDelay = 1000)
    public void processOutbox() {
        List<OutboxEvent> events = outboxRepository.findByProcessedFalse();

        if (events.isEmpty()) return;

        for (OutboxEvent event : events) {
            try {
                String topic = switch (event.getEventType()) {
                    case "USER_CREATED" -> "USER_CREATED_TOPIC";
                    case "USER_UPDATED" -> "USER_UPDATED_TOPIC";
                    case "USER_DELETED" -> "USER_DELETED_TOPIC";
                    default -> throw new IllegalArgumentException("Unknown event type: " + event.getEventType());
                };
                kafkaTemplate.send(topic, event.getPayload());
                event.setProcessed(true);
            } catch (Exception e) {
                throw new RuntimeException("Error sending message", e);
            }
        }
        outboxRepository.saveAll(events);
    }
}
