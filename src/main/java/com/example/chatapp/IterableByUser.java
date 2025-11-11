package com.example.chatapp;

import java.util.Iterator;

public interface IterableByUser {
    // Return an iterator over messages involving the given user
    Iterator<Message> iterator(User userToSearchWith);
}
