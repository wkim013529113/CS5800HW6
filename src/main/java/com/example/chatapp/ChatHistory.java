package com.example.chatapp;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ChatHistory implements IterableByUser {
    private final List<Message> sent = new ArrayList<>();
    private final List<Message> received = new ArrayList<>();

    public void addSent(Message m) { sent.add(m); }
    public void addReceived(Message m) { received.add(m); }

    public Optional<Message> getLastSent() {
        if (sent.isEmpty()) return Optional.empty();
        return Optional.of(sent.get(sent.size() - 1));
    }

    public List<Message> getReceived() { return List.copyOf(received); }
    public List<Message> getSent() { return List.copyOf(sent); }

    public List<Message> conversationWith(String other) {
        List<Message> convo = new ArrayList<>();
        for (Message m : sent) {
            if (m.getRecipients().contains(other)) convo.add(m);
        }
        for (Message m : received) {
            if (m.getSender().equals(other)) convo.add(m);
        }
        return convo;
    }

    @Override
    public java.util.Iterator<Message> iterator(User userToSearchWith) {
        return new searchMessagesByUser(this, userToSearchWith);
    }
}
