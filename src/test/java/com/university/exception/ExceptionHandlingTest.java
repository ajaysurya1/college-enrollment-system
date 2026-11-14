package com.university.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ExceptionHandlingTest {

    @Test
    public void testExceptionInheritanceHierarchy() {
        CourseFullException fullEx = new CourseFullException("Full");
        assertTrue(fullEx instanceof EnrollmentException);
        assertTrue(fullEx instanceof Exception);

        DuplicateEnrollmentException dupEx = new DuplicateEnrollmentException("Duplicate");
        assertTrue(dupEx instanceof EnrollmentException);

        PrerequisiteNotMetException prereqEx = new PrerequisiteNotMetException("Prerequisite missing");
        assertTrue(prereqEx instanceof EnrollmentException);

        RegistrationClosedException regEx = new RegistrationClosedException("Closed");
        assertTrue(regEx instanceof EnrollmentException);

        AuthenticationException authEx = new AuthenticationException("Bad login");
        assertTrue(authEx instanceof Exception);

        DatabaseException dbEx = new DatabaseException("DB error");
        assertTrue(dbEx instanceof RuntimeException);
    }

    @Test
    public void testExceptionMessages() {
        String msg = "Prerequisite CS101 required";
        PrerequisiteNotMetException ex = new PrerequisiteNotMetException(msg);
        assertEquals(msg, ex.getMessage());
    }
}

