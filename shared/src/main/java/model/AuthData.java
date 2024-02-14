package model;

public record AuthData(String authToken, String username) {}

/* In a record:
 * All fields are marked 'final'
 * Simplified constructor syntax
 * Automatic getters
 * Automatic equals that compares all fields
 * Automatic hashcode that calculates based on all fields
 * Automatic toString that represents all the fields
 */