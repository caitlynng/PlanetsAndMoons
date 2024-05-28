package com.revature.service;

import com.revature.models.User;
import com.revature.models.UsernamePasswordAuthentication;
import com.revature.repository.UserDao;
import com.google.common.base.CharMatcher;

import java.util.regex.Pattern;


public class UserService {

    private UserDao dao;

    public UserService(UserDao dao) {
        this.dao = dao;
    }

    public User authenticate(UsernamePasswordAuthentication loginRequestData) {
        // TODO: implement
        // Remove trailing space and make username lowercase
        String username = loginRequestData.getUsername().trim().toLowerCase();
        String password = loginRequestData.getPassword();

        System.out.println(username);
        if (fieldsCheck(username) == null && fieldsCheck(password) == null) {
            return null;
        }

        User user = dao.getUserByUsername(username);

        // Check if the user exists and the provided password matches
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }

        // If user doesn't exist or password doesn't match, return null
        return null;
    }

    public User register(User registerRequestData) {
        // Remove trailing space and make username lowercase
        String username = registerRequestData.getUsername().trim().toLowerCase();
        String password = registerRequestData.getPassword();

        if (fieldsCheck(username) == null && fieldsCheck(password) == null) {
            return null;
        }

        // Check if the username is unique
        if (dao.getUserByUsername(username) == null) {
            // Create the new user
            UsernamePasswordAuthentication validUserData = new UsernamePasswordAuthentication();
            validUserData.setUsername(username);
            validUserData.setPassword(password);
            return dao.createUser(validUserData);
        }

        // Username already exists
        return null;
    }

    private String fieldsCheck(String str) {
        // Check if username or password is empty
        if (str.isEmpty()) {
            return null;
        }

        // Check that username and password are within acceptable length
        if (str.length() > 30) {
            return null;
        }

        // Validate username and password for ASCII characters and specific pattern
        CharMatcher asciiMatcher = CharMatcher.ascii();
        Pattern sqlInjectionPattern = Pattern.compile("\\b\\w+\\b.*\\b(?:DROP|DELETE|INSERT|UPDATE|TRUNCATE|ALTER|EXEC|UNION|SELECT|CREATE)\\b.*", Pattern.CASE_INSENSITIVE);

        if (!asciiMatcher.matchesAllOf(str) ||
                sqlInjectionPattern.matcher(str).find()) {
            return null;
        }

        // Check for SQL injection patterns
        if (containsSQLInjection(str)) {
            return null;
        }
        return str;
    }

    private boolean containsSQLInjection(String str) {
        // Implement logic to detect SQL injection, e.g., using regex or libraries
        // For simplicity, let's assume a basic check for common SQL keywords
        return str.matches(".*\\b(SELECT|INSERT|UPDATE|DELETE|DROP|ALTER)\\b.*");
    }

}
