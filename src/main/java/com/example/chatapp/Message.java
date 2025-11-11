package com.example.chatapp;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

public class Message {
    private final String sender;
    private final List<String> recipients;
    private final Instant timestamp;
    private final String content;
    private final boolean system;
    private final boolean retractionNotice;

    private Message(Builder b) {
        this.sender = b.sender;
        this.recipients = List.copyOf(b.recipients);
        this.timestamp = b.timestamp;
        this.content = b.content;
        this.system = b.system;
        this.retractionNotice = b.retractionNotice;
    }

    public String getSender() { return sender; }
    public List<String> getRecipients() { return recipients; }
    public Instant getTimestamp() { return timestamp; }
    public String getContent() { return content; }
    public boolean isSystem() { return system; }
    public boolean isRetractionNotice() { return retractionNotice; }

    public static Builder builder(String sender, List<String> recipients, String content) {
        return new Builder(sender, recipients, content);
    }

    public static class Builder {
        private final String sender;
        private final List<String> recipients;
        private final String content;
        private Instant timestamp = Instant.now();
        private boolean system = false;
        private boolean retractionNotice = false;

        public Builder(String sender, List<String> recipients, String content) {
            this.sender = Objects.requireNonNull(sender);
            this.recipients = Objects.requireNonNull(recipients);
            this.content = Objects.requireNonNull(content);
        }

        public Builder timestamp(Instant ts) { this.timestamp = ts; return this; }
        public Builder system(boolean v) { this.system = v; return this; }
        public Builder retractionNotice(boolean v) { this.retractionNotice = v; return this; }

        public Message build() { return new Message(this); }
    }

    @Override
    public String toString() {
        String recips = String.join(",", recipients);
        String tag = system ? "[SYSTEM]" : "";
        String retract = retractionNotice ? "[RETRACT]" : "";
        return "%s%s %s -> [%s] @ %s: %s".formatted(tag, retract, sender, recips, timestamp, content);
    }
}
