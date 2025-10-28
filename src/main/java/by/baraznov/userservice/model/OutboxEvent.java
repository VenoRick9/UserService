package by.baraznov.userservice.model;

import by.baraznov.userservice.dto.user.UserCreatedEvent;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "outbox_event")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutboxEvent {
    @Id
    private UUID id;
    @Column(name = "aggregate_id")
    private UUID aggregateId;
    @Column(name = "aggregate_type")
    private String aggregateType;
    @Column(name = "event_type")
    private String eventType;
    @Type(JsonType.class)
    @Column(name = "payload", columnDefinition = "jsonb")
    private UserCreatedEvent payload;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    private boolean processed;
}
