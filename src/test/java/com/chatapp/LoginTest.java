package com.chatapp;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * JUnit 5 tests for ChatApp 
 *
 * @author Leonard Petersen
 */
class LoginTest {

    private Login login;

    @BeforeEach
    void setUp() {
        login = new Login();
    }

    @Test
    void validUsername() {
        assertTrue(login.checkUserName("Kyl_1"));
    }

    @Test
    void invalidUsernameWithoutUnderscore() {
        assertFalse(login.checkUserName("ky1"));
    }

    @Test
    void invalidUsernameTooLong() {
        assertFalse(login.checkUserName("Kyl_12"));
    }

    @Test
    void invalidUsernameSpecialCharactersAndTooLong() {
        assertFalse(login.checkUserName("kyle!!!!!"));
    }

    @Test
    void validPassword() {
        assertTrue(login.checkPasswordComplexity("Ch&&sec@ke99!"));
    }

    @Test
    void invalidPasswordMissingCapitalLetter() {
        assertFalse(login.checkPasswordComplexity("chatapp9!"));
    }

    @Test
    void invalidPasswordMissingNumber() {
        assertFalse(login.checkPasswordComplexity("ChatApp!!"));
    }

    @Test
    void invalidPasswordMissingSpecialCharacter() {
        assertFalse(login.checkPasswordComplexity("ChatApp99"));
    }

    @Test
    void invalidPasswordTooShort() {
        assertFalse(login.checkPasswordComplexity("C@t9abc"));
    }

    @Test
    void validCellPhoneNumber() {
        assertTrue(login.checkCellPhoneNumber("+27821234567"));
    }

    @Test
    void invalidCellPhoneNumberWrongPrefix() {
        assertFalse(login.checkCellPhoneNumber("0821234567"));
    }

    @Test
    void invalidCellPhoneNumberTooLong() {
        assertFalse(login.checkCellPhoneNumber("+278212345678"));
    }

    @Test
    void invalidCellPhoneNumberTooShort() {
        assertFalse(login.checkCellPhoneNumber("+2782123456"));
    }

    @Test
    void successfulRegistrationMessage() {
        assertEquals(
                "User registered successfully.",
                login.registerUser(
                        "Kyl_1",
                        "Ch&&sec@ke99!",
                        "+27821234567",
                        "Leonard",
                        "Petersen"));
    }

    @Test
    void failedRegistrationMessageForInvalidUsername() {
        assertEquals(
                "Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.",
                login.registerUser(
                        "kyl12",
                        "Ch&&sec@ke99!",
                        "+27821234567",
                        "Leonard",
                        "Petersen"));
    }

    @Test
    void failedRegistrationMessageForInvalidPassword() {
        assertEquals(
                "Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.",
                login.registerUser(
                        "kyl1",
                        "Ch&&sec@ke99!",
                        "+27821234567",
                        "Leonard",
                        "Petersen"));
    }

    @Test
    void failedRegistrationMessageForInvalidCellPhone() {
        assertEquals(
                "Cell phone number incorrectly formatted or does not contain international code.",
                login.registerUser(
                        "Kyl_1",
                        "Ch&&sec@ke99!",
                        "0821234567",
                        "Leonard",
                        "Petersen"));
    }

    @Test
    void successfulLoginAuthentication() {
        login.registerUser(
                "Kyl_1",
                "Ch&&sec@ke99!",
                "+27821234567",
                "Leonard",
                "Petersen");

        assertTrue(login.loginUser("Kyl_1", "Ch&&sec@ke99!"));
    }

    @Test
    void failedLoginAuthentication() {
        login.registerUser(
                "Kyl_1",
                "Ch&&sec@ke99!",
                "+27821234567",
                "Leonard",
                "Petersen");

        assertFalse(login.loginUser("Kyl_11", "WrongPassword1!"));
    }

    @Test
    void successfulLoginMessageUsesFirstAndLastName() {
        login.registerUser(
                "Kyl_1",
                "Ch&&sec@ke99!",
                "+27821234567",
                "Leonard",
                "Petersen");

        assertEquals(
                "Welcome Leonard Petersen, it is great to see you again.",
                login.returnLoginStatus("Kyl_1", "Ch&&sec@ke99!"));
    }

    @Test
    void failedLoginMessage() {
        login.registerUser(
                "Kyl_1",
                "Ch&&sec@ke99",
                "+27821234567",
                "Leonard",
                "Petersen");

        assertEquals(
                "Username or password incorrect, please try again.",
                login.returnLoginStatus("Kyl_1", "WrongPassword1!"));
    }
}
