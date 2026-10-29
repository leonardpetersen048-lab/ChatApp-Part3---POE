package com.chatapp;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Reads and writes message records as a JSON array. */
public class MessageJsonStore {
    private final Path filePath;
    private final ObjectMapper mapper = new ObjectMapper();

    public MessageJsonStore() { this(Paths.get("stored_messages.json")); }
    public MessageJsonStore(Path filePath) { this.filePath = filePath; }

    /** Stores all messages with a STORED status in a readable JSON array. */
    public void storeMessages(List<Message> messages) throws IOException {
        List<Map<String, Object>> records = messages.stream()
                .filter(m -> "STORED".equalsIgnoreCase(m.getStatus()))
                .map(this::toRecord).collect(Collectors.toList());
        Path parent = filePath.toAbsolutePath().getParent();
        if (parent != null) Files.createDirectories(parent);
        mapper.writerWithDefaultPrettyPrinter().writeValue(filePath.toFile(), records);
    }

    /** Loads the JSON file into Message objects; an absent file means no saved messages. */
    public List<Message> loadStoredMessages() throws IOException {
        if (!Files.exists(filePath) || Files.size(filePath) == 0) return new ArrayList<>();
        List<Map<String, Object>> records = mapper.readValue(filePath.toFile(),
                new TypeReference<List<Map<String, Object>>>() {});
        List<Message> result = new ArrayList<>();
        for (Map<String, Object> record : records) {
            String id = stringValue(record.get("messageId"));
            int number = intValue(record.get("messageNumber"));
            String recipient = stringValue(record.get("recipient"));
            String message = stringValue(record.get("message"));
            String hash = stringValue(record.get("messageHash"));
            String status = stringValue(record.getOrDefault("status", "STORED"));
            result.add(Message.fromStoredData(id, number, recipient, message, hash, status));
        }
        return result;
    }

    public void deleteStoredMessageByHash(String hash) throws IOException {
        List<Message> stored = loadStoredMessages();
        stored.removeIf(m -> m.getMessageHash().equalsIgnoreCase(hash));
        writeRecords(stored);
    }

    private void writeRecords(List<Message> messages) throws IOException {
        List<Map<String, Object>> records = messages.stream().map(this::toRecord).collect(Collectors.toList());
        Path parent = filePath.toAbsolutePath().getParent();
        if (parent != null) Files.createDirectories(parent);
        mapper.writerWithDefaultPrettyPrinter().writeValue(filePath.toFile(), records);
    }

    private Map<String, Object> toRecord(Message m) {
        Map<String, Object> record = new LinkedHashMap<>();
        record.put("messageId", m.getMessageId());
        record.put("messageNumber", m.getMessageNumber());
        record.put("recipient", m.getRecipient());
        record.put("message", m.getMessageText());
        record.put("messageHash", m.getMessageHash());
        record.put("status", m.getStatus());
        return record;
    }
    private String stringValue(Object value) { return value == null ? "" : String.valueOf(value); }
    private int intValue(Object value) {
        if (value instanceof Number) return ((Number) value).intValue();
        try { return Integer.parseInt(stringValue(value)); } catch (NumberFormatException e) { return 0; }
    }
    public Path getFilePath() { return filePath; }
}
