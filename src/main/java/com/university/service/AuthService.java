package com.university.service;

import com.university.dao.UserDAO;
import com.university.dao.impl.UserDAOImpl;
import com.university.exception.AuthenticationException;
import com.university.model.Person;
import com.university.util.PasswordUtil;

public class AuthService {

    private final UserDAO userDAO;

    public AuthService() {
        this.userDAO = new UserDAOImpl();
    }

    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public Person login(String username, String password) throws AuthenticationException {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new AuthenticationException("Username and password must not be empty");
        }

        Person person = userDAO.findByUsername(username.trim());
        if (person == null) {
            throw new AuthenticationException("Invalid username or password");
        }

        if (!person.isActive()) {
            throw new AuthenticationException("This account has been deactivated. Please contact administrator.");
        }

        String hash = userDAO.getPasswordHash(username.trim());
        if (hash == null || !PasswordUtil.checkPassword(password, hash)) {
            throw new AuthenticationException("Invalid username or password");
        }

        return person;
    }

    public boolean changePassword(int userId, String oldPassword, String newPassword) throws AuthenticationException {
        if (newPassword == null || newPassword.length() < 4) {
            throw new AuthenticationException("New password must be at least 4 characters long");
        }

        Person person = userDAO.findById(userId);
        if (person == null) {
            throw new AuthenticationException("User not found");
        }

        String currentHash = userDAO.getPasswordHash(person.getUsername());
        if (!PasswordUtil.checkPassword(oldPassword, currentHash)) {
            throw new AuthenticationException("Current password does not match");
        }

        String newHash = PasswordUtil.hashPassword(newPassword);
        return userDAO.updatePassword(userId, newHash);
    }
}
