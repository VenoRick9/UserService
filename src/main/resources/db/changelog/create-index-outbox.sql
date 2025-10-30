CREATE INDEX idx_outbox_unprocessed_type
    ON outbox_event(event_type)
    WHERE processed = FALSE;