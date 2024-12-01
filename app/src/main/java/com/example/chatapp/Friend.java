package com.example.chatapp;

public class Friend {
    private String id;  // 친구의 ID
    private String name;  // 친구의 이름
    private String status;  // 친구의 상태 (예: 온라인, 오프라인 등)

    // Firebase를 위한 기본 생성자
    public Friend() {}

    // 친구 객체를 위한 생성자
    public Friend(String id, String name, String status) {
        this.id = id;
        this.name = name;
        this.status = status;
    }

    // Getter 및 Setter 메서드
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
