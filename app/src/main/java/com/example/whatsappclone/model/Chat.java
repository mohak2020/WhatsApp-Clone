package com.example.whatsappclone.model;

public class Chat {

    private String chatId;
    private String userName;

    public Chat(String chatId, String userName) {
        this.chatId = chatId;
        this.userName = userName;
    }

    public String getChatId() {
        return chatId;
    }

    public String getUserName() {
        return userName;
    }
}
