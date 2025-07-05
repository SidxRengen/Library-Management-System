package main.java.com.library.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtils {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static String formatDate(LocalDate date) {
        return date != null ? date.format(DATE_FORMATTER) : "";
    }

    public static LocalDate parseDate(String dateString) {
        return dateString != null && !dateString.isEmpty() ?
                LocalDate.parse(dateString, DATE_FORMATTER) : null;
    }
}