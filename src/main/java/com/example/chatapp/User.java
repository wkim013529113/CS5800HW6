package com.example.chatapp;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

public class User {
    private final String name;
    private final ChatMediator mediator;
    private final ChatHistory history = new ChatHistory();
    // caretaker stack for mementos (supports multiple undos if desired)
    private final Deque<MessageMemento> sentStack = new ArrayDeque<>();

    public User(String name, ChatMediator mediator) {
        this.name = Objects.requireNonNull(name);
        this.mediator = Objects.requireNonNull(mediator);
    }

    public String getName() { return name; }
    public ChatHistory getHistory() { return history; }

    public void send(String content, List<String> to) {
        // create memento BEFORE sending
        MessageMemento m = new MessageMemento(content, Instant.now());
        sentStack.push(m);

        mediator.sendMessage(name, to, content);
    }

    public void undoLastSent() {
        if (sentStack.isEmpty()) return;
        MessageMemento last = sentStack.pop();
        mediator.retractMessage(name, last);
    }

    // Receive is only called by the mediator
    public void receive(Message message) {
        history.addReceived(message);
        // For demo output:
        System.out.println("[%s] received: %s".formatted(name, message));
    }

    // Blocking is requested via mediator (users do not talk directly)
    public void block(String other) { mediator.block(name, other); }
    public void unblock(String other) { mediator.unblock(name, other); }

    // Convenience: view conversation with another user (merged sent/received against that peer)
    public void printConversationWith(String other) {
        System.out.println("---- Conversation (" + name + " <-> " + other + ") ----");
        for (Message m : history.conversationWith(other)) {
            System.out.println(m);
        }
    }
}
