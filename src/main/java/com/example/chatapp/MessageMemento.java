package com.example.chatapp;

import java.time.Instant;

public class MessageMemento {
    private final String content;
    private final Instant timestamp;

    public MessageMemento(String content, Instant timestamp) {
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getContent() { return content; }
    public Instant getTimestamp() { return timestamp; }
}
