CREATE TABLE outbox_events (
   id UUID PRIMARY KEY,
   topic VARCHAR(255) NOT NULL,
   event_type VARCHAR(255) NOT NULL,
   payload TEXT NOT NULL,
   created_at TIMESTAMP WITH TIME ZONE NOT NULL,
   processed BOOLEAN NOT NULL
);