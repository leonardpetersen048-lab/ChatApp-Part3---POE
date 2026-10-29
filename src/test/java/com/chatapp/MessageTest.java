package com.chatapp;

import static org.junit.jupiter.api.Assertions.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class MessageTest {
    @BeforeEach
    void clearMessages() {
        Message.clearSessionMessages();
    }

    @Test
    void generatedMessageIdIsTenDigits() {
        Message message = new Message(0, "+27718693002", "Hi Mike, can you join us for dinner tonight?");
        assertTrue(message.checkMessageID());
        assertEquals(10, message.getMessageId().length());
        assertTrue(message.getMessageId().matches("\\d{10}"));
    }

    @Test
    void explicitMessageIdCanBeValidated() {
        assertTrue(new Message("1234567890", 0, "+27718693002", "Hi there").checkMessageID());
        assertFalse(new Message("12345678901", 0, "+27718693002", "Hi there").checkMessageID());
    }

    @Test
    void recipientNumberAcceptsSouthAfricanInternationalFormat() {
        Message message = new Message("0012345678", 0, "+27718693002", "Hi there");
        assertEquals("Cell phone number successfully captured.", message.checkRecipientCell());
    }

    @Test
    void recipientNumberRejectsLocalFormat() {
        Message message = new Message("0012345678", 0, "08575975889", "Hi there");
        assertEquals("Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.", message.checkRecipientCell());
    }

    @Test
    void messageLengthUnderLimitIsReady() {
        Message message = new Message("0012345678", 0, "+27718693002", "Hi Mike, can you join us for dinner tonight?");
        assertEquals("Message ready to send.", message.checkMessageLength());
    }

    @Test
    void messageLengthOverLimitShowsExcess() {
        Message message = new Message("0012345678", 0, "+27718693002", "x".repeat(255));
        assertEquals("Message exceeds 250 characters by 5; please reduce the size.", message.checkMessageLength());
    }

    @Test
    void hashMatchesAssignmentExample() {
        Message message = new Message("0012345678", 0, "+27718693002", "Hi Mike, can you join us for dinner tonight?");
        assertEquals("00:0:HITONIGHT", message.createMessageHash());
    }

    @Test
    void sendActionSetsSentStatusAndReturnsRequiredMessage() {
        Message message = new Message("0012345678", 0, "+27718693002", "Hi Mike");
        assertEquals("Message successfully sent.", message.SentMessage("1"));
        assertEquals("SENT", message.getStatus());
        assertEquals(1, Message.returnTotalMessagess());
    }

    @Test
    void discardActionSetsDiscardedStatusAndReturnsRequiredMessage() {
        Message message = new Message("0012345678", 0, "08575975889", "Hi Keegan");
        assertEquals("Press 0 to delete the message.", message.SentMessage("2"));
        assertEquals("DISCARDED", message.getStatus());
        assertEquals(0, Message.returnTotalMessagess());
    }

    @Test
    void storeActionSetsStoredStatusAndReturnsRequiredMessage() {
        Message message = new Message("0012345678", 0, "+27718693002", "Hi Mike");
        assertEquals("Message successfully stored.", message.SentMessage("3"));
        assertEquals("STORED", message.getStatus());
    }

    @Test
    void sentActionRejectsInvalidRecipient() {
        Message message = new Message("0012345678", 0, "08575975889", "Hi Keegan");
        assertTrue(message.SentMessage("1").startsWith("Cell phone number is incorrectly formatted"));
    }

    @Test
    void printMessagesIncludesMessageDetails() {
        Message message = new Message("0012345678", 0, "+27718693002", "Hi Mike");
        message.SentMessage("1");
        String result = message.printMessages();
        assertTrue(result.contains("Message ID: 0012345678"));
        assertTrue(result.contains("Message Hash: 00:0:HIMIKE"));
        assertTrue(result.contains("Recipient: +27718693002"));
        assertTrue(result.contains("Message: Hi Mike"));
    }

    @Test
    void jsonStoreWritesOnlyStoredMessages(@TempDir Path tempDir) throws Exception {
        Message stored = new Message("0012345678", 0, "+27718693002", "Hi Mike");
        stored.SentMessage("3");
        Message sent = new Message("0012345679", 1, "+27718693002", "Hello again");
        sent.SentMessage("1");
        Path json = tempDir.resolve("stored_messages.json");
        new MessageJsonStore(json).storeMessages(List.of(stored, sent));
        String content = Files.readString(json);
        assertTrue(content.contains("\"status\":\"STORED\""));
        assertTrue(content.contains("Hi Mike"));
        assertFalse(content.contains("Hello again"));
    }
}
