package com.revature.units.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import com.revature.exceptions.*;

public class CustomExceptionsTest {

    @Test
    public void testMoonFailException() {
        String message = "Moon operation failed";
        MoonFailException exception = assertThrows(MoonFailException.class, () -> {
            throw new MoonFailException(message);
        });
        assertEquals(message, exception.getMessage());
    }

    @Test
    public void testNotLoggedInException_defaultMessage() {
        NotLoggedInException exception = assertThrows(NotLoggedInException.class, () -> {
            throw new NotLoggedInException();
        });
        assertEquals("You must be logged in to complete this operation", exception.getMessage());
    }

    @Test
    public void testNotLoggedInException_customMessage() {
        String message = "Custom not logged in message";
        NotLoggedInException exception = assertThrows(NotLoggedInException.class, () -> {
            throw new NotLoggedInException(message);
        });
        assertEquals(message, exception.getMessage());
    }

    @Test
    public void testNotLoggedInException_cause() {
        Throwable cause = new Throwable("Cause");
        NotLoggedInException exception = assertThrows(NotLoggedInException.class, () -> {
            throw new NotLoggedInException(cause);
        });
        assertEquals(cause, exception.getCause());
    }

    @Test
    public void testNotLoggedInException_customMessageAndCause() {
        String message = "Custom not logged in message";
        Throwable cause = new Throwable("Cause");
        NotLoggedInException exception = assertThrows(NotLoggedInException.class, () -> {
            throw new NotLoggedInException(message, cause);
        });
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    public void testNotLoggedInException_fullConstructor() {
        String message = "Custom not logged in message";
        Throwable cause = new Throwable("Cause");
        boolean enableSuppression = true;
        boolean writableStackTrace = false;
        NotLoggedInException exception = new NotLoggedInException(message, cause, enableSuppression, writableStackTrace);

        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
        assertTrue(exception.getSuppressed().length == 0);
    }

    @Test
    public void testPlanetFailException() {
        String message = "Planet operation failed";
        PlanetFailException exception = assertThrows(PlanetFailException.class, () -> {
            throw new PlanetFailException(message);
        });
        assertEquals(message, exception.getMessage());
    }

    @Test
    public void testUserFailException() {
        String message = "User operation failed";
        UserFailException exception = assertThrows(UserFailException.class, () -> {
            throw new UserFailException(message);
        });
        assertEquals(message, exception.getMessage());
    }
}

