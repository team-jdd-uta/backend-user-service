package com.teamuta.userinfoserver.dto;

public class ChatMessage {
    public enum MessageType {
        ENTER, TALK, QUIT
    }

    private MessageType type; // 메시지 타입
    private String roomId;    // 방 번호
    private String sender;    // 메시지 보낸 사람
    // 변경 요청 반영:
    // - "유실률 측정을 위해 msgId 추적이 필요" 요청에 따라 메시지 고유 식별자를 추가한다.
    // 이유:
    // - TALK 메시지 단위 전달 여부를 추적해 유실률을 계산하기 위해 서버-클라이언트 간 동일 ID를 유지해야 한다.
    private String msgId;     // 메시지 고유 ID(유실률 추적용)
    private String message;   // 메시지 내용

    public ChatMessage() {
    }

    public ChatMessage(MessageType type, String roomId, String sender, String msgId, String message) {
        this.type = type;
        this.roomId = roomId;
        this.sender = sender;
        this.msgId = msgId;
        this.message = message;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
