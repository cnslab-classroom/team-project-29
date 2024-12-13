package com.example.chatapp;

import java.util.List;

public class ChatRoom {
    private String roomId;
    private List<String> participantIds;

    public ChatRoom(String defaultChatRoom) {
        // Firebase를 위한 기본 생성자
    }

    public ChatRoom(String roomId, List<String> participantIds) {
        this.roomId = roomId;
        this.participantIds = participantIds;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public List<String> getParticipantIds() {
        return participantIds;
    }

    public void setParticipantIds(List<String> participantIds) {
        this.participantIds = participantIds;
    }
}
