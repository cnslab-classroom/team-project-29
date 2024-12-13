package com.example.chatapp;

public class Message {
    private String senderId;
    private String message;
    private long timestamp;
    private String imageUrl; // 이미지 URL 필드 추가

    // Firebase를 위한 기본 생성자
    public Message() {}

    // 메시지를 위한 생성자
    public Message(String senderId, String message, long timestamp) {
        this.senderId = senderId;
        this.message = message;
        this.timestamp = timestamp;
        this.imageUrl = imageUrl; // 이미지 URL 초기화
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}