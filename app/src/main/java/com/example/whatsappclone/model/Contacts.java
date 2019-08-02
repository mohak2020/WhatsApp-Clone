package com.example.whatsappclone.model;

public class Contacts {

    private String userName;
    private String userPhoneNumber;

    public Contacts() {
    }

    public Contacts(String userName, String userPhoneNumber) {
        this.userName = userName;
        this.userPhoneNumber = userPhoneNumber;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public void setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }
}
