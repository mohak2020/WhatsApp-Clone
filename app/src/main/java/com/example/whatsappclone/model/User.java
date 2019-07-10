package com.example.whatsappclone.model;

public class User {

    private String userName;
    private String userPhoneNumber;

    public User(String userName, String userPhoneNumber) {
        this.userName = userName;
        this.userPhoneNumber = userPhoneNumber;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

}
