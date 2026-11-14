package com.university.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EncapsulationTest {

    @Test
    public void testValidPersonEncapsulation() {
        Student student = new Student();
        student.setUserId(10);
        student.setUsername("testuser");
        student.setFullName("Test User");
        student.setEmail("test@university.edu");
        student.setRollNumber("R1001");
        student.setProgramme("B.Tech");
        student.setYearOfStudy(2);

        assertEquals(10, student.getUserId());
        assertEquals("testuser", student.getUsername());
        assertEquals("Test User", student.getFullName());
        assertEquals("test@university.edu", student.getEmail());
        assertEquals("R1001", student.getRollNumber());
        assertEquals("B.Tech", student.getProgramme());
        assertEquals(2, student.getYearOfStudy());
    }

    @Test
    public void testInvalidEmailThrowsException() {
        Student student = new Student();
        assertThrows(IllegalArgumentException.class, () -> {
            student.setEmail("invalid-email-address");
        });
    }

    @Test
    public void testEmptyNameThrowsException() {
        Student student = new Student();
        assertThrows(IllegalArgumentException.class, () -> {
            student.setFullName("   ");
        });
    }

    @Test
    public void testInvalidYearOfStudyThrowsException() {
        Student student = new Student();
        assertThrows(IllegalArgumentException.class, () -> {
            student.setYearOfStudy(0);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            student.setYearOfStudy(-2);
        });
    }

    @Test
    public void testCourseEncapsulationAndValidation() {
        Course course = new Course();
        course.setCourseCode("CS101");
        course.setCourseTitle("Programming Basics");
        course.setCredits(4);

        assertEquals("CS101", course.getCourseCode());
        assertEquals("Programming Basics", course.getCourseTitle());
        assertEquals(4, course.getCredits());

        assertThrows(IllegalArgumentException.class, () -> {
            course.setCourseTitle("");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            course.setCredits(0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            course.setCredits(-1);
        });
    }
}

