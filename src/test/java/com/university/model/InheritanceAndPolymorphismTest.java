package com.university.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class InheritanceAndPolymorphismTest {

    @Test
    public void testStudentPolymorphism() {
        Person person = new Student(1, "alice", "Alice Johnson", "alice@test.edu", 1, true, "S001", "CS", 2);

        assertEquals("STUDENT", person.getRoleName());
        assertTrue(person.getDashboardMessage().contains("Alice Johnson"));
        assertTrue(person.getDashboardMessage().contains("S001"));
        assertTrue(person instanceof Student);
        assertTrue(person instanceof Person);
    }

    @Test
    public void testFacultyPolymorphism() {
        Person person = new Faculty(2, "smith", "John Smith", "smith@test.edu", 1, true, "Professor");

        assertEquals("FACULTY", person.getRoleName());
        assertTrue(person.getDashboardMessage().contains("Professor"));
        assertTrue(person.getDashboardMessage().contains("John Smith"));
        assertTrue(person instanceof Faculty);
        assertTrue(person instanceof Person);
    }

    @Test
    public void testAdminPolymorphism() {
        Person person = new Administrator(3, "admin", "Admin User", "admin@test.edu", 1, true);

        assertEquals("ADMIN", person.getRoleName());
        assertTrue(person.getDashboardMessage().contains("Administrator"));
        assertTrue(person.getDashboardMessage().contains("Admin User"));
        assertTrue(person instanceof Administrator);
        assertTrue(person instanceof Person);
    }

    @Test
    public void testPolymorphicArrayExecution() {
        Person[] users = new Person[]{
                new Student(1, "s1", "Student One", "s1@test.edu", 1, true, "R1", "CS", 1),
                new Faculty(2, "f1", "Faculty One", "f1@test.edu", 1, true, "Lecturer"),
                new Administrator(3, "a1", "Admin One", "a1@test.edu", 1, true)
        };

        String[] expectedRoles = {"STUDENT", "FACULTY", "ADMIN"};

        for (int i = 0; i < users.length; i++) {
            assertEquals(expectedRoles[i], users[i].getRoleName());
            assertNotNull(users[i].getDashboardMessage());
        }
    }
}

