package com.example.chatapp;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ChatAppTest {

    @Test
    void send_and_block_and_undo_flow() {
        ChatServer server = new ChatServer();
        User a = new User("Bill", server);
        User b = new User("Don", server);
        User c = new User("Henry", server);
        server.register(a);
        server.register(b);
        server.register(c);

        // Alice -> Bob,Carol
        a.send("hello", List.of("Don", "Henry"));
        assertEquals(1, b.getHistory().getReceived().size());
        assertEquals(1, c.getHistory().getReceived().size());

        // Bob blocks Alice
        b.block("Bill");

        // Alice -> Bob,Carol (Bob should not receive)
        a.send("are you there?", List.of("Don", "Henry"));
        assertEquals(1, b.getHistory().getReceived().size(), "Don NOT receive after blocking");
        assertEquals(2, c.getHistory().getReceived().size(), "Henry should receive the second message");

        // Undo last Alice message -> system retraction notice to recipients not blocking sender (Carol)
        a.undoLastSent();

        // Bob should not receive system notice (he blocked Alice)
        // Carol should get a system retract notice
        long carolRetracts = c.getHistory().getReceived().stream().filter(Message::isRetractionNotice).count();
        long bobRetracts   = b.getHistory().getReceived().stream().filter(Message::isRetractionNotice).count();

        assertEquals(1, carolRetracts);
        assertEquals(0, bobRetracts);
    }
}
