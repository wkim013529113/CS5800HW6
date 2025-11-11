package com.example.chatapp;

import java.util.*;
import java.util.stream.Collectors;

public class searchMessagesByUser implements Iterator<Message> {
    private final List<Message> snapshot; // immutable snapshot (per instructions)
    private int cursor = 0;

    public searchMessagesByUser(ChatHistory history, User other) {
        String otherName = other.getName();

        // Build a stable snapshot of messages where:
        // - sent by "owner" to other, OR
        // - received by "owner" from other
        List<Message> combined = new ArrayList<>();
        combined.addAll(history.getSent());
        combined.addAll(history.getReceived());

        this.snapshot = combined.stream()
                .filter(m ->
                        m.getSender().equals(otherName) ||
                                m.getRecipients().contains(otherName)
                )
                // optional: keep time order for nicer iteration
                .sorted(Comparator.comparing(Message::getTimestamp))
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public boolean hasNext() {
        return cursor < snapshot.size();
    }

    @Override
    public Message next() {
        if (!hasNext()) throw new NoSuchElementException();
        return snapshot.get(cursor++);
    }
}
