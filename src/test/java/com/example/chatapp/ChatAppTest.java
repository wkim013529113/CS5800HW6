package com.example.chatapp;

import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ChatAppTest {

    @Test
    void send_block_undo_flow_matches_requirements() {
        ChatServer server = new ChatServer();
        User bill  = new User("Bill", server);
        User don   = new User("Don", server);
        User henry = new User("Henry", server);
        server.register(bill);
        server.register(don);
        server.register(henry);

        // Bill -> Don,Henry
        bill.send("hello", List.of("Don", "Henry"));
        assertEquals(1, don.getHistory().getReceived().size(), "Don should receive the first message");
        assertEquals(1, henry.getHistory().getReceived().size(), "Henry should receive the first message");

        // Don blocks Bill
        don.block("Bill");

        // Bill -> Don,Henry (Don should not receive; Henry should)
        bill.send("are you there?", List.of("Don", "Henry"));
        assertEquals(1, don.getHistory().getReceived().size(), "Don must not receive after blocking");
        assertEquals(2, henry.getHistory().getReceived().size(), "Henry should receive the second message");

        // Undo last Bill message -> retraction notice to recipients not blocking Bill (Henry)
        bill.undoLastSent();

        long henryRetracts = henry.getHistory().getReceived().stream().filter(Message::isRetractionNotice).count();
        long donRetracts   = don.getHistory().getReceived().stream().filter(Message::isRetractionNotice).count();

        assertEquals(1, henryRetracts, "Henry should see a single retraction notice");
        assertEquals(0, donRetracts,   "Don blocked Bill, so no retraction notice");
    }

    @Test
    void iterator_returns_only_messages_involving_target_user() {
        ChatServer server = new ChatServer();
        User bill  = new User("Bill", server);
        User don   = new User("Don", server);
        User henry = new User("Henry", server);
        server.register(bill);
        server.register(don);
        server.register(henry);

        // Exchange some messages
        bill.send("BILL->DON one",   List.of("Don"));
        bill.send("BILL->HENRY one", List.of("Henry"));
        don.send("DON->BILL one",    List.of("Bill"));
        henry.send("HENRY->BILL one",List.of("Bill"));

        // Iterate Bill's messages with Don
        Iterator<Message> itBD = bill.iterator(don);
        int countBD = 0;
        while (itBD.hasNext()) {
            Message m = itBD.next();
            assertTrue(
                    m.getSender().equals("Don") || m.getRecipients().contains("Don"),
                    "Iterator(Bill, Don) must only return messages involving Don"
            );
            countBD++;
        }
        assertEquals(2, countBD, "Bill <-> Don should have exactly 2 messages");

        // Iterate Bill's messages with Henry
        Iterator<Message> itBH = bill.iterator(henry);
        int countBH = 0;
        while (itBH.hasNext()) {
            Message m = itBH.next();
            assertTrue(
                    m.getSender().equals("Henry") || m.getRecipients().contains("Henry"),
                    "Iterator(Bill, Henry) must only return messages involving Henry"
            );
            countBH++;
        }
        assertEquals(2, countBH, "Bill <-> Henry should have exactly 2 messages");
    }
}
