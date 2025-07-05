package main.java.com.library.models;

public interface LibraryUser {
    String getUserId();
    String getName();
    String getEmail();
    UserType getUserType();

    enum UserType {
        STUDENT, FACULTY, STAFF, GUEST
    }
}