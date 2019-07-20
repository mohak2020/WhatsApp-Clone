package com.example.whatsappclone.model;

public class Message {

    String messageId;
    String creator;
    String messageText;

    public Message(String messageId, String creator, String messageText) {
        this.messageId = messageId;
        this.creator = creator;
        this.messageText = messageText;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getCreator() {
        return creator;
    }

    public String getMessageText() {
        return messageText;
    }
}
