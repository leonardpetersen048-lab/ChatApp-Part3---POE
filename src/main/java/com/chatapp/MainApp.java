package com.chatapp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/** Console entry point: preserves Part 1 registration/login and adds Part 2 messaging. */
public class MainApp {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            Login login = new Login();
            System.out.println("=== ChatApp - Formative 3 Part 3 ===");
            System.out.print("Enter first name: ");
            String firstName = scanner.nextLine();
            System.out.print("Enter last name: ");
            String lastName = scanner.nextLine();
            System.out.print("Enter username: ");
            String username = scanner.nextLine();
            System.out.print("Enter password: ");
            String password = scanner.nextLine();
            System.out.print("Enter South African cell phone number (+27 followed by 9 digits): ");
            String phone = scanner.nextLine();

            String registration = login.registerUser(username, password, phone, firstName, lastName);
            System.out.println(registration);
            if (!"User registered successfully.".equals(registration)) {
                System.out.println("Registration failed. Restart the application and try again.");
                return;
            }

            System.out.println("\n=== Login ===");
            System.out.print("Enter username: ");
            String loginUsername = scanner.nextLine();
            System.out.print("Enter password: ");
            String loginPassword = scanner.nextLine();
            System.out.println(login.returnLoginStatus(loginUsername, loginPassword));
            if (!login.loginUser(loginUsername, loginPassword)) return;

            runQuickChatMenu(scanner);
        }
    }

    private static void runQuickChatMenu(Scanner scanner) {
        List<Message> messages = new ArrayList<>();
        Message.clearSessionMessages();
        MessageJsonStore jsonStore = new MessageJsonStore();
        boolean running = true;
        System.out.println("\nWelcome to QuickChat.");
        while (running) {
            System.out.println("\n1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            System.out.println("4) Stored Messages / Task Report");
            System.out.print("Choose an option: ");
            String option = scanner.nextLine().trim();
            switch (option) {
                case "1":
                    int count = readMessageCount(scanner);
                    int initialSize = messages.size();
                    for (int i = 0; i < count; i++) {
                        Message message = promptForMessage(scanner, initialSize + i);
                        messages.add(message);
                        while ("PENDING".equals(message.getStatus())) {
                            String actionResult = message.SentMessage(promptForAction(scanner));
                            System.out.println(actionResult);
                            if ("PENDING".equals(message.getStatus())) {
                                System.out.println("This message was not sent or stored. Choose another action, or disregard it.");
                            }
                        }
                        System.out.println("\n" + message.toDisplayString());
                    }
                    try {
                        jsonStore.storeMessages(messages);
                        System.out.println("Stored messages JSON file: " + jsonStore.getFilePath().toAbsolutePath());
                    } catch (IOException exception) {
                        System.out.println("Unable to store messages: " + exception.getMessage());
                    }
                    System.out.println("Total messages successfully sent: " + Message.returnTotalMessagess());
                    break;
                case "2":
                    displayRecentSentMessages(messages);
                    break;
                case "3":
                    running = false;
                    System.out.println("Thank you for using QuickChat.");
                    break;
                case "4":
                    storedMessagesMenu(scanner, messages, jsonStore);
                    break;
                default:
                    System.out.println("Invalid option. Please choose 1, 2, 3, or 4.");
            }
        }
    }


    private static void displayRecentSentMessages(List<Message> messages) {
        List<Message> sent = new ArrayList<>();
        for (Message message : messages) {
            if ("SENT".equals(message.getStatus())) sent.add(message);
        }
        if (sent.isEmpty()) {
            System.out.println("No messages have been sent yet.");
            return;
        }
        System.out.println("\nRecently sent messages:");
        sent.forEach(message -> System.out.println(message.toDisplayString() + "\n"));
    }

    /** Part 3 menu for loading, searching, deleting, and reporting stored data. */
    private static void storedMessagesMenu(Scanner scanner, List<Message> sessionMessages,
                                           MessageJsonStore jsonStore) {
        boolean inMenu = true;
        while (inMenu) {
            List<Message> stored;
            try {
                stored = jsonStore.loadStoredMessages();
            } catch (IOException ex) {
                System.out.println("Could not read stored messages: " + ex.getMessage());
                return;
            }
            // Populate the assignment's data arrays dynamically from current session and JSON data.
            List<Message> sentMessages = filterByStatus(sessionMessages, "SENT");
            List<Message> disregardedMessages = filterByStatus(sessionMessages, "DISCARDED");
            List<String> messageHashes = new ArrayList<>();
            List<String> messageIds = new ArrayList<>();
            for (Message message : sessionMessages) {
                messageHashes.add(message.getMessageHash());
                messageIds.add(message.getMessageId());
            }
            for (Message message : stored) {
                if (!messageHashes.contains(message.getMessageHash())) messageHashes.add(message.getMessageHash());
                if (!messageIds.contains(message.getMessageId())) messageIds.add(message.getMessageId());
            }
            System.out.println("\n=== Stored Messages ===");
            System.out.println("1) Display sender and recipient of stored messages");
            System.out.println("2) Display the longest stored message");
            System.out.println("3) Search by message ID");
            System.out.println("4) Search messages by recipient");
            System.out.println("5) Delete a stored message using its hash");
            System.out.println("6) Display full task report");
            System.out.println("0) Return to main menu");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    if (stored.isEmpty()) System.out.println("No stored messages found.");
                    else for (Message m : stored) {
                        System.out.println("Sender: Developer | Recipient: " + m.getRecipient()
                                + " | Message ID: " + m.getMessageId());
                    }
                    break;
                case "2":
                    Message longest = stored.stream().max((a,b) -> Integer.compare(
                            a.getMessageText().length(), b.getMessageText().length())).orElse(null);
                    System.out.println(longest == null ? "No stored messages found."
                            : "Longest stored message (" + longest.getMessageText().length() + " characters):\n"
                            + longest.getMessageText());
                    break;
                case "3":
                    System.out.print("Enter message ID: ");
                    String id = scanner.nextLine().trim();
                    Message byId = stored.stream().filter(m -> m.getMessageId().equals(id)).findFirst().orElse(null);
                    System.out.println(byId == null ? "No stored message found for that ID."
                            : "Recipient: " + byId.getRecipient() + "\nMessage: " + byId.getMessageText());
                    break;
                case "4":
                    System.out.print("Enter recipient number: ");
                    String recipient = scanner.nextLine().trim();
                    List<Message> matches = stored.stream().filter(m -> m.getRecipient().equals(recipient))
                            .collect(java.util.stream.Collectors.toList());
                    if (matches.isEmpty()) System.out.println("No stored messages found for that recipient.");
                    else matches.forEach(m -> System.out.println(m.getMessageText()));
                    break;
                case "5":
                    System.out.print("Enter message hash to delete: ");
                    String hash = scanner.nextLine().trim();
                    boolean exists = stored.stream().anyMatch(m -> m.getMessageHash().equalsIgnoreCase(hash));
                    if (!exists) System.out.println("No stored message found with that hash.");
                    else try {
                        Message deleted = stored.stream().filter(m -> m.getMessageHash().equalsIgnoreCase(hash)).findFirst().get();
                        jsonStore.deleteStoredMessageByHash(hash);
                        System.out.println("Message: \"" + deleted.getMessageText() + "\" successfully deleted.");
                    } catch (IOException ex) { System.out.println("Could not delete message: " + ex.getMessage()); }
                    break;
                case "6":
                    displayTaskReport(sessionMessages, stored, sentMessages, disregardedMessages, messageHashes, messageIds);
                    break;
                case "0": inMenu = false; break;
                default: System.out.println("Invalid option. Choose 0 to 6.");
            }
        }
    }

    private static List<Message> filterByStatus(List<Message> messages, String status) {
        List<Message> result = new ArrayList<>();
        for (Message message : messages) if (status.equals(message.getStatus())) result.add(message);
        return result;
    }

    private static void displayTaskReport(List<Message> sessionMessages, List<Message> stored,
                                          List<Message> sent, List<Message> disregarded,
                                          List<String> hashes, List<String> ids) {
        System.out.println("\n========== QUICKCHAT TASK REPORT ==========");
        System.out.println("Sent messages: " + sent.size());
        System.out.println("Disregarded messages: " + disregarded.size());
        System.out.println("Stored messages loaded from JSON: " + stored.size());
        System.out.println("Message IDs tracked: " + ids.size());
        System.out.println("Message hashes tracked: " + hashes.size());
        if (stored.isEmpty() && sent.isEmpty()) System.out.println("No stored or sent message details to report.");
        else {
            System.out.println("\n--- Full details of sent messages ---");
            for (Message m : sent) {
                System.out.println("Message Hash: " + m.getMessageHash());
                System.out.println("Recipient: " + m.getRecipient());
                System.out.println("Message: " + m.getMessageText());
                System.out.println("Message ID: " + m.getMessageId());
                System.out.println("Status: " + m.getStatus());
                System.out.println("---------------------------------------");
            }
            System.out.println("\n--- Full details of stored messages ---");
            for (Message m : stored) {
                System.out.println("Message Hash: " + m.getMessageHash());
                System.out.println("Recipient: " + m.getRecipient());
                System.out.println("Message: " + m.getMessageText());
                System.out.println("Message ID: " + m.getMessageId());
                System.out.println("Status: " + m.getStatus());
                System.out.println("---------------------------------------");
            }
        }
        System.out.println("===========================================");
    }

    private static int readMessageCount(Scanner scanner) {
        while (true) {
            System.out.print("How many messages would you like to enter? ");
            String input = scanner.nextLine().trim();
            try {
                int count = Integer.parseInt(input);
                if (count > 0 && count <= 100) return count;
            } catch (NumberFormatException ignored) {
                // Re-prompt below.
            }
            System.out.println("Please enter a whole number between 1 and 100.");
        }
    }

    private static Message promptForMessage(Scanner scanner, int messageNumber) {
        System.out.print("Enter recipient cell number (+27 followed by 9 digits): ");
        String recipient = scanner.nextLine().trim();
        Message recipientValidator = new Message("0012345678", messageNumber, recipient, "validation");
        Message.removeSessionMessage(recipientValidator);
        if (!recipientValidator.isRecipientValid()) {
            System.out.println(recipientValidator.checkRecipientCell());
            System.out.println("You may disregard this message; sending or storing requires a valid recipient number.");
        }
        String text;
        while (true) {
            System.out.print("Enter message (maximum 250 characters): ");
            text = scanner.nextLine();
            Message validator = new Message("0012345678", messageNumber, recipient, text);
            Message.removeSessionMessage(validator);
            String result = validator.checkMessageLength();
            if ("Message ready to send.".equals(result)) break;
            System.out.println(result);
        }
        return new Message(messageNumber, recipient, text);
    }

    private static String promptForAction(Scanner scanner) {
        while (true) {
            System.out.println("Choose message action: 1) Send Message  2) Disregard Message  3) Store Message");
            System.out.print("Option: ");
            String choice = scanner.nextLine().trim();
            if (choice.matches("[1-3]")) return choice;
            System.out.println("Please choose 1, 2, or 3.");
        }
    }
}
