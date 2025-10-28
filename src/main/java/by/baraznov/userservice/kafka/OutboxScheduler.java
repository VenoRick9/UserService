package by.baraznov.userservice.kafka;

import by.baraznov.userservice.dto.user.UserCreatedEvent;
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
    private final KafkaTemplate<String, UserCreatedEvent> kafkaTemplate;
    private static final String TOPIC = "CREATE_USER";

    @Scheduled(fixedDelay = 1000)
    public void processOutbox() {
        List<OutboxEvent> events = outboxRepository.findByProcessedFalse();
        if(events != null && !events.isEmpty()) {
            for (OutboxEvent event : events) {
                try {
                    kafkaTemplate.send(TOPIC, event.getPayload());
                    event.setProcessed(true);
                } catch (Exception e) {
                    throw new RuntimeException("Error sending message", e);
                }
            }
            outboxRepository.saveAll(events);
        }
    }
}
