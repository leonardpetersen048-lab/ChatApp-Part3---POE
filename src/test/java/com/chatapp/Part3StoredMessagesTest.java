package com.chatapp;

import static org.junit.jupiter.api.Assertions.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class Part3StoredMessagesTest {
    @Test
    void storedJsonCanBeLoadedIntoMessageObjects(@TempDir Path tempDir) throws Exception {
        Message stored = new Message("1234567890", 2, "+27838884567",
                "Where are you? You are late! I have asked you to be on time.");
        stored.SentMessage("3");
        Path file = tempDir.resolve("stored_messages.json");
        MessageJsonStore store = new MessageJsonStore(file);
        store.storeMessages(List.of(stored));
        List<Message> loaded = store.loadStoredMessages();
        assertEquals(1, loaded.size());
        assertEquals("+27838884567", loaded.get(0).getRecipient());
        assertEquals("Where are you? You are late! I have asked you to be on time.", loaded.get(0).getMessageText());
        assertEquals(stored.getMessageHash(), loaded.get(0).getMessageHash());
    }

    @Test
    void deletingByHashRemovesOnlyMatchingStoredMessage(@TempDir Path tempDir) throws Exception {
        Message first = new Message("1234567890", 2, "+27838884567", "Where are you? You are late!");
        first.SentMessage("3");
        Message second = new Message("1234567891", 5, "+27838884567", "Ok, I am leaving without you.");
        second.SentMessage("3");
        MessageJsonStore store = new MessageJsonStore(tempDir.resolve("stored_messages.json"));
        store.storeMessages(List.of(first, second));
        store.deleteStoredMessageByHash(first.getMessageHash());
        List<Message> remaining = store.loadStoredMessages();
        assertEquals(1, remaining.size());
        assertEquals(second.getMessageHash(), remaining.get(0).getMessageHash());
    }

    @Test
    void absentJsonFileLoadsAsEmptyList(@TempDir Path tempDir) throws Exception {
        assertTrue(new MessageJsonStore(tempDir.resolve("missing.json")).loadStoredMessages().isEmpty());
    }

    @Test
    void reportDataSupportsProvidedAssignmentTestMessages() {
        List<Message> data = new ArrayList<>();
        Message m1 = new Message("1000000001", 1, "+27834557896", "Did you get the cake?"); m1.SentMessage("1"); data.add(m1);
        Message m2 = new Message("1000000002", 2, "+27838884567", "Where are you? You are late! I have asked you to be on time."); m2.SentMessage("3"); data.add(m2);
        Message m3 = new Message("1000000003", 3, "+27834484567", "Yohoooo, I am at your gate."); m3.SentMessage("2"); data.add(m3);
        Message m4 = new Message("1000000004", 4, "+27838884567", "It is dinner time!"); m4.SentMessage("1"); data.add(m4);
        Message m5 = new Message("1000000005", 5, "+27838884567", "Ok, I am leaving without you."); m5.SentMessage("3"); data.add(m5);
        assertEquals(2, data.stream().filter(m -> "STORED".equals(m.getStatus())).count());
        assertEquals(2, data.stream().filter(m -> "SENT".equals(m.getStatus())).count());
        assertEquals(1, data.stream().filter(m -> "DISCARDED".equals(m.getStatus())).count());
        assertEquals("It is dinner time!", data.stream().filter(m -> m.getMessageId().equals("1000000004")).findFirst().get().getMessageText());
    }
}
