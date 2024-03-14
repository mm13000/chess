package dataAccess;

import java.util.UUID;

public class UUIDGenerator {
    // This class exists for testing purposes, to allow forced duplicate UUIDs
    public UUID generateUUID() {
        return UUID.randomUUID();
    }
}