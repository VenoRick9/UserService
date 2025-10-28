CREATE TABLE outbox_event
(
    id             UUID PRIMARY KEY,
    aggregate_id   UUID         NOT NULL,
    aggregate_type VARCHAR(255) NOT NULL,
    event_type     VARCHAR(255) NOT NULL,
    payload        TEXT        NOT NULL,
    created_at     TIMESTAMP    NOT NULL DEFAULT NOW(),
    processed      BOOLEAN               DEFAULT FALSE
);

