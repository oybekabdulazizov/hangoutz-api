package com.hangoutz.app.exception;

public interface ExceptionMessage {

    String USER_NOT_FOUND = "User not found";
    String CATEGORY_NOT_FOUND = "Category not found";
    String EVENT_NOT_FOUND = "Event not found";
    String EMAIL_TAKEN = "Email taken";
    String CATEGORY_ALREADY_EXISTS = "Category already exists";
    String INVALID_DATE_FORMAT = "Invalid date format. Please use 'yyyy-MM-ddTHH:mm(:SS)' as 'T' is just a delimiter";
    String HOST_MUST_BE_PRESENT = "You, as the host, must be present at the event. Cancelling can be an option";
    String INTERNAL_ERROR = "Internal error occurred";
    String NULL_POINTER_EXCEPTION = "Null pointer exception";
    String INVALID_EMAIL = "Please provide a valid email address";


    /* auth related messages */
    String INVALID_TOKEN = "Invalid token";
    String BAD_CREDENTIALS = "Username or password is incorrect";
    String PASSWORDS_MUST_MATCH = "New password and its confirmation must match";
    String PERMISSION_DENIED = "Permission denied";
    String TOKEN_EXPIRED = "Token expired";
}
