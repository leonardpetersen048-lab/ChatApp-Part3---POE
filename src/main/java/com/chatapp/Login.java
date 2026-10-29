package com.chatapp;

/**
 * Handles registration, validation and login for ChatApp .
 *
 * @author Leonard Petersen
 */
public class Login {

    private String username;
    private String password;
    private String phoneNumber;
    private String firstName;
    private String lastName;

    /**
     * Username requirements:
     * - must contain an underscore
     * - must be no more than five characters
     */
    public boolean checkUserName(String username) {
        return username != null
                && username.contains("_")
                && username.length() <= 5;
    }

    /**
     * Password requirements:
     * - at least 8 characters
     * - at least one capital letter
     * - at least one number
     * - at least one special character
     */
    public boolean checkPasswordComplexity(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        boolean hasCapital = false;
        boolean hasNumber = false;
        boolean hasSpecial = false;

        for (char character : password.toCharArray()) {
            if (Character.isUpperCase(character)) {
                hasCapital = true;
            } else if (Character.isDigit(character)) {
                hasNumber = true;
            } else if (!Character.isLetterOrDigit(character)) {
                hasSpecial = true;
            }
        }

        return hasCapital && hasNumber && hasSpecial;
    }

    /**
     * Cell-phone validation uses a regular expression.
     *
     * Pattern source/research:
     * https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/regex/Pattern.html
     *
     * ^       start of input
     * \\+27    South African international prefix
     * \\d{9}  exactly nine digits after +27
     * $       end of input
     *
     * This therefore requires exactly 12 characters in total.
     */
    public boolean checkCellPhoneNumber(String phoneNumber) {
        return phoneNumber != null
                && phoneNumber.matches("^\\+27\\d{9}$");
    }

    /**
     * Registers a user using the required registration details.
     */
    public String registerUser(String username, String password, String phoneNumber,
                               String firstName, String lastName) {

        if (!checkUserName(username)) {
            return "Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.";
        }

        if (!checkPasswordComplexity(password)) {
            return "Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.";
        }

        if (!checkCellPhoneNumber(phoneNumber)) {
            return "Cell phone number incorrectly formatted or does not contain international code.";
        }

        this.username = username;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.firstName = firstName;
        this.lastName = lastName;

        return "User registered successfully.";
    }

    /**
     * Backwards-compatible overload for tests that only supply
     * username, password and phone number.
     */
    public String registerUser(String username, String password, String phoneNumber) {
        return registerUser(username, password, phoneNumber, "", "");
    }

    /**
     * Checks whether the supplied login details match the registered user.
     */
    public boolean loginUser(String username, String password) {
        return this.username != null
                && this.password != null
                && this.username.equals(username)
                && this.password.equals(password);
    }

    /**
     * Returns the required login confirmation or failure message.
     */
    public String returnLoginStatus(String username, String password) {
        if (loginUser(username, password)) {
            if (firstName != null && !firstName.isBlank()
                    && lastName != null && !lastName.isBlank()) {
                return "Welcome " + firstName + " " + lastName
                        + ", it is great to see you again.";
            }

            return "Welcome " + username + ", it is great to see you again.";
        }

        return "Username or password incorrect, please try again.";
    }
}
