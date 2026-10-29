package com.chatapp;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/** Represents one QuickChat message and its status. */
public class Message {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final Pattern SA_PHONE_PATTERN = Pattern.compile("^\\+27\\d{9}$");
    private static final List<Message> SESSION_MESSAGES = new ArrayList<>();

    private final String messageId;
    private final int messageNumber;
    private final String recipient;
    private final String messageText;
    private String messageHash;
    private String status;

    /** Creates a message with a generated ten-digit ID. */
    public Message(int messageNumber, String recipient, String messageText) {
        this(generateMessageId(), messageNumber, recipient, messageText);
    }

    /** Constructor with an explicit ID, useful for predictable unit tests. */
    public Message(String messageId, int messageNumber, String recipient, String messageText) {
        this.messageId = messageId == null ? "" : messageId;
        this.messageNumber = messageNumber;
        this.recipient = recipient == null ? "" : recipient.trim();
        this.messageText = messageText == null ? "" : messageText.trim();
        this.messageHash = createMessageHash();
        this.status = "PENDING";
        SESSION_MESSAGES.add(this);
    }

    private static String generateMessageId() {
        // A ten-digit numeric ID; leading zeroes are preserved as a String.
        StringBuilder id = new StringBuilder("00");
        for (int i = 0; i < 8; i++) id.append(RANDOM.nextInt(10));
        return id.toString();
    }

    public boolean checkMessageID() {
        return !messageId.isBlank() && messageId.length() <= 10 && messageId.matches("\\d+");
    }

    /** Part 1's South African format is +27 followed by nine digits. */
    public String checkRecipientCell() {
        if (SA_PHONE_PATTERN.matcher(recipient).matches()) {
            return "Cell phone number successfully captured.";
        }
        return "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
    }

    public boolean isRecipientValid() {
        return SA_PHONE_PATTERN.matcher(recipient).matches();
    }

    public String checkMessageLength() {
        if (messageText.length() <= 250) return "Message ready to send.";
        return "Message exceeds 250 characters by " + (messageText.length() - 250)
                + "; please reduce the size.";
    }

    /** Hash format follows the assignment example: first two ID digits:number:first+last words. */
    public String createMessageHash() {
        String prefix = messageId.length() >= 2 ? messageId.substring(0, 2) : String.format("%02d", 0);
        String firstWord = "";
        String lastWord = "";
        String trimmed = messageText == null ? "" : messageText.trim();
        if (!trimmed.isEmpty()) {
            String[] words = trimmed.split("\\s+");
            firstWord = cleanWord(words[0]);
            lastWord = cleanWord(words[words.length - 1]);
        }
        return (prefix + ":" + messageNumber + ":" + firstWord + lastWord).toUpperCase(Locale.ROOT);
    }

    private String cleanWord(String word) {
        return word.replaceAll("[^\\p{L}\\p{N}]", "");
    }

    /** Sets status and returns the required assignment message. */
    public String SentMessage(String choice) {
        if (choice == null) return "Invalid message option.";
        switch (choice.trim().toLowerCase(Locale.ROOT)) {
            case "1":
            case "send":
            case "send message":
                if (!isRecipientValid()) return checkRecipientCell();
                if (!checkMessageLength().equals("Message ready to send.")) return checkMessageLength();
                status = "SENT";
                return "Message successfully sent.";
            case "2":
            case "discard":
            case "disregard":
            case "disregard message":
                status = "DISCARDED";
                return "Press 0 to delete the message.";
            case "3":
            case "store":
            case "store message":
                if (!isRecipientValid()) return checkRecipientCell();
                if (!checkMessageLength().equals("Message ready to send.")) return checkMessageLength();
                status = "STORED";
                return "Message successfully stored.";
            default:
                return "Invalid message option.";
        }
    }

    /** Returns the details of all messages created during this application session. */
    public String printMessages() {
        StringBuilder output = new StringBuilder();
        for (Message message : SESSION_MESSAGES) {
            output.append(message.toDisplayString()).append(System.lineSeparator());
        }
        return output.toString().trim();
    }

    public static List<Message> getSessionMessages() {
        return new ArrayList<>(SESSION_MESSAGES);
    }

    public static void clearSessionMessages() {
        SESSION_MESSAGES.clear();
    }

    public static void removeSessionMessage(Message message) {
        SESSION_MESSAGES.remove(message);
    }

    public static int returnTotalMessagess() {
        int total = 0;
        for (Message message : SESSION_MESSAGES) {
            if ("SENT".equals(message.status)) total++;
        }
        return total;
    }

    public String toDisplayString() {
        return "Message ID: " + messageId + System.lineSeparator()
                + "Message Hash: " + messageHash + System.lineSeparator()
                + "Recipient: " + recipient + System.lineSeparator()
                + "Message: " + messageText + System.lineSeparator()
                + "Status: " + status;
    }

    /** JSON representation with correct escaping for quotes, slashes and control characters. */
    public String toJsonObject() {
        return "{\"messageId\":\"" + jsonEscape(messageId)
                + "\",\"messageNumber\":" + messageNumber
                + ",\"recipient\":\"" + jsonEscape(recipient)
                + "\",\"message\":\"" + jsonEscape(messageText)
                + "\",\"messageHash\":\"" + jsonEscape(messageHash)
                + "\",\"status\":\"" + jsonEscape(status) + "\"}";
    }

    private static String jsonEscape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }

    public String getMessageId() { return messageId; }
    public int getMessageNumber() { return messageNumber; }
    public String getRecipient() { return recipient; }
    public String getMessageText() { return messageText; }
    public String getMessageHash() { return messageHash; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    /** Recreates a message loaded from the stored JSON file without changing its ID. */
    public static Message fromStoredData(String messageId, int messageNumber, String recipient,
                                         String messageText, String messageHash, String status) {
        Message message = new Message(messageId, messageNumber, recipient, messageText);
        if (messageHash != null && !messageHash.isBlank()) {
            // Keep the hash from the saved record so the report matches the stored data.
            message.messageHash = messageHash;
        }
        if (status != null && !status.isBlank()) message.status = status;
        return message;
    }
}
