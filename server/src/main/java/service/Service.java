package service;

public class Service {
    protected boolean invalidRequest(Record record) {
        if (record == null) return true;
        // Iterate over the fields in the record and check if any are null or empty. If so, return false
        for (var field : record.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true); // Allow us to access the private fields
                var value = field.get(record);
                field.setAccessible(false); // Set the accessibility back to private
                if (value == null || value.toString().isEmpty()) return true;
            } catch (IllegalAccessException ignored) {}
        }
        return false;
    }
}
