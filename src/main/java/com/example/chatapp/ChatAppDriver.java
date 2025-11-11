package com.example.chatapp;

import java.util.Iterator;
import java.util.List;

public class ChatAppDriver {
    public static void main(String[] args) {
        ChatServer server = new ChatServer();

        User bill  = new User("Bill", server);
        User don   = new User("Don", server);
        User henry = new User("Henry", server);

        server.register(bill);
        server.register(don);
        server.register(henry);

        // 1) Bill sends to Don and Henry via mediator
        bill.send("Hi both!", List.of("Don", "Henry"));

        // 2) Don blocks Bill
        don.block("Bill");

        // 3) Bill sends again (Don should NOT receive it; Henry should)
        bill.send("Are you joining the meeting?", List.of("Don", "Henry"));

        // 4) Undo last message (retraction notice via mediator to recipients not blocking Bill -> Henry only)
        bill.undoLastSent();

        // 5) Henry replies to Bill
        henry.send("Yes, I’ll be there!", List.of("Bill"));

        // 6) Histories
        System.out.println();
        bill.printConversationWith("Don");
        bill.printConversationWith("Henry");
        don.printConversationWith("Bill");
        henry.printConversationWith("Bill");

        // 7) Iterator demo: iterate Bill's messages involving Don
        System.out.println("\nIterating Bill's messages with Don (Iterator):");
        Iterator<Message> itBillDon = bill.iterator(don);
        while (itBillDon.hasNext()) {
            System.out.println("• " + itBillDon.next());
        }

        // 8) Iterator demo: iterate Bill's messages involving Henry
        System.out.println("\nIterating Bill's messages with Henry (Iterator):");
        Iterator<Message> itBillHenry = bill.iterator(henry);
        while (itBillHenry.hasNext()) {
            System.out.println("• " + itBillHenry.next());
        }
    }
}
