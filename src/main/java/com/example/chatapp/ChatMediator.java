package com.example.chatapp;

import java.util.List;

public interface ChatMediator {
    void register(User user);
    void unregister(String username);
    void sendMessage(String sender, List<String> recipients, String content);
    void retractMessage(String sender, MessageMemento memento);
    void block(String blocker, String blockedUser);
    void unblock(String blocker, String blockedUser);
}
