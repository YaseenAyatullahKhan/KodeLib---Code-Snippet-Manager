package util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {
    //shared timestamp format pattern for both methods
    private static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss";
    
    //date and time formatter for generating current time (used when saving to JSON)
    public static String getFormattedDateTimeNow() {
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIMESTAMP_FORMAT);
        return dateTime.format(formatter);
    }
    
    //utility method: parse timestamp string from JSON to LocalDate (used when loading from JSON)
    public static LocalDate parseTimestamp(String timestamp) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIMESTAMP_FORMAT);
            LocalDateTime dateTime = LocalDateTime.parse(timestamp, formatter);
            return dateTime.toLocalDate();
        } catch (Exception e) {
            return LocalDate.now();
        }
    }
}