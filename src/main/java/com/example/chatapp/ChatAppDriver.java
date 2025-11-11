package com.example.chatapp;

import java.util.List;

public class ChatAppDriver {
    public static void main(String[] args) {
        ChatServer server = new ChatServer();

        User bill = new User("Bill", server);
        User don   = new User("Don", server);
        User henry = new User("Henry", server);

        server.register(bill);
        server.register(don);
        server.register(henry);

        // 1) Sends to others via mediator
        bill.send("Hi there!", List.of("Don", "Henry"));

        // 2) Blocks person
        don.block("Bill");

        // 3) Sends again to others (blocked person should NOT get it)
        bill.send("Are you joining the call?", List.of("Don", "Henry"));

        // 4) Undo last message (retraction notice via mediator)
        bill.undoLastSent();

        // 5) henry replies to bill
        henry.send("Yes, I’ll be there!", List.of("Bill"));

        // 6) Histories
        System.out.println();
        bill.printConversationWith("Don");
        bill.printConversationWith("Henry");
        don.printConversationWith("Bill");
        henry.printConversationWith("Bill");
    }
}
