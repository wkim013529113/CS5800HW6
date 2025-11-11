package com.example.chatapp;

import java.time.Instant;
import java.util.*;

public class ChatServer implements ChatMediator {
    private final Map<String, User> users = new HashMap<>();
    // key = recipient, value = set of users that recipient has blocked
    private final Map<String, Set<String>> blockMap = new HashMap<>();

    @Override
    public void register(User user) {
        users.put(user.getName(), user);
        blockMap.putIfAbsent(user.getName(), new HashSet<>());
    }

    @Override
    public void unregister(String username) {
        users.remove(username);
        blockMap.remove(username);
    }

    @Override
    public void sendMessage(String sender, List<String> recipients, String content) {
        Objects.requireNonNull(sender);
        Objects.requireNonNull(recipients);
        Objects.requireNonNull(content);

        // Build the outgoing message once
        Message outgoing = Message.builder(sender, recipients, content)
                .timestamp(Instant.now())
                .build();

        // Append to sender history
        User senderUser = users.get(sender);
        if (senderUser != null) {
            senderUser.getHistory().addSent(outgoing);
        }

        // Deliver to each recipient unless they've blocked the sender
        for (String r : recipients) {
            User recipient = users.get(r);
            if (recipient == null) continue;
            if (isBlocked(r, sender)) {
                // Optional: drop silently or record a system notice for the sender
                continue;
            }
            recipient.receive(outgoing);
        }
    }

    @Override
    public void retractMessage(String sender, MessageMemento memento) {
        // Send a system retraction notice to everyone who might have received it.
        // We don't try to delete from their history; we append a clear retract notice.
        if (!users.containsKey(sender)) return;

        // Collect all users except sender to notify
        for (User u : users.values()) {
            if (u.getName().equals(sender)) continue;
            // Only notify if not blocking the sender
            if (isBlocked(u.getName(), sender)) continue;

            Message notice = Message.builder("SYSTEM", List.of(u.getName()),
                            "User '%s' retracted a message sent at %s".formatted(sender, memento.getTimestamp()))
                    .system(true)
                    .retractionNotice(true)
                    .timestamp(Instant.now())
                    .build();
            u.receive(notice);
        }
    }

    @Override
    public void block(String blocker, String blockedUser) {
        blockMap.computeIfAbsent(blocker, k -> new HashSet<>()).add(blockedUser);
    }

    @Override
    public void unblock(String blocker, String blockedUser) {
        blockMap.computeIfAbsent(blocker, k -> new HashSet<>()).remove(blockedUser);
    }

    private boolean isBlocked(String recipient, String sender) {
        return blockMap.getOrDefault(recipient, Collections.emptySet()).contains(sender);
    }
}
