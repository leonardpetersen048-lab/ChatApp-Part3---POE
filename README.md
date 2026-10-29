# ChatApp - Part 3 - POE

## Overview

ChatApp is a Java console application developed as a continuation of the earlier formative parts. It includes user registration and login, message handling, JSON storage, and a report menu for working with saved messages.

The project is built with Maven and includes JUnit tests for important application features.

## Features

### Registration and login
- Captures the user's first name, last name, username, password, and South African cell phone number.
- Checks the registration details and provides a login step.

### Message handling
- Creates messages with generated message IDs and message hashes.
- Validates recipient phone numbers using the format `+27` followed by nine digits.
- Checks that messages do not exceed 250 characters.
- Allows messages to be sent, stored, or discarded.
- Displays recently sent messages and their details.

### Part 3: Stored messages and task report
- Saves message records in `stored_messages.json`.
- Loads saved messages from the JSON file.
- Displays stored message recipients.
- Finds the longest stored message.
- Searches for a message by its ID.
- Searches stored messages by recipient.
- Deletes a stored message using its message hash.
- Displays a task report containing message hashes, recipients, messages, IDs, and statuses.

## Technologies used

- **Java 17**
- **Maven** for project and dependency management
- **Jackson Databind** for JSON reading and writing
- **JUnit 5** for unit testing
- **NetBeans** as the recommended IDE

## Project structure

```text
ChatApp - part 3 - POE
├── src/
│   ├── main/java/com/chatapp/
│   │   ├── MainApp.java
│   │   ├── Login.java
│   │   ├── Message.java
│   │   └── MessageJsonStore.java
│   └── test/java/com/chatapp/
│       ├── LoginTest.java
│       ├── MessageTest.java
│       └── Part3StoredMessagesTest.java
├── pom.xml
└── README.md
```

## Requirements

Before running the project, make sure you have:

- JDK 17 or later installed.
- Apache NetBeans or another Java IDE that supports Maven.
- An internet connection for Maven to download dependencies the first time you build the project.

## Open and run in NetBeans

1. Download or clone this repository from GitHub.
2. Extract the ZIP file if you downloaded the project as a ZIP.
3. Open NetBeans.
4. Select **File → Open Project**.
5. Select the project folder containing `pom.xml`.
6. Allow Maven to resolve the dependencies.
7. Right-click the project and select **Clean and Build**.
8. Right-click the project and select **Run**, or run `com.chatapp.MainApp`.

## How to use the application

1. Start the application.
2. Enter the requested registration details.
3. Log in with the registered username and password.
4. Choose **Send Messages** to create messages.
5. Follow the prompts to send, store, or discard each message.
6. Choose **Show recently sent messages** to view sent messages.
7. Choose **Stored Messages / Task Report** to load and manage stored records.
8. Use the submenu to search, display, or delete stored messages and view the task report.

Stored messages are read from and written to `stored_messages.json` in the application's working directory. Keep this file if you want to retain the stored message records between runs.

## Running the tests

In NetBeans, right-click the project and select **Test**.

You can also run the tests from a terminal in the project directory:

```bash
mvn test
```

The tests cover login and message behaviour, along with stored-message features such as loading, deletion, and handling an empty data file.

## GitHub Actions

The repository includes a Maven test workflow at `.github/workflows/maven-tests.yml`. GitHub Actions can run the test suite when changes are pushed to the repository or a pull request is opened, depending on the workflow configuration.

## Notes

- This is a **console-based application**; it does not use a graphical user interface.
- The JSON data file may be created when messages are stored. If it is not present yet, use the application to store a message first.
- Test the application in your own NetBeans environment before submitting.
- The ZIP file contains project files but does not include the Git commit history. Make meaningful commits in your GitHub repository as required by the assessment.
- If your assessment requires a demonstration video, record your own demonstration and provide the correct video link when submitting.

## Author
Leonard Petersen - ST10504745


