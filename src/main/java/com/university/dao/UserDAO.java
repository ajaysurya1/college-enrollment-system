package com.university.dao;

import com.university.model.Person;

public interface UserDAO {
    Person findByUsername(String username);
    Person findById(int userId);
    String getPasswordHash(String username);
    boolean updatePassword(int userId, String newHash);
    int countByRole(String role);
}
