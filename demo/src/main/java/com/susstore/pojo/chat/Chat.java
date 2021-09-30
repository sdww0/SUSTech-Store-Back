package com.susstore.pojo.chat;

import java.util.List;

public class Chat {

    private Integer speakUserId;
    private String speakUserName;
    private String speakUserPicturePath;
    private Integer otherUserId;
    private String otherUserName;
    private String otherUserPicturePath;
    private List<ChatContent> chatContents;

    public Chat() {
    }

    public Chat(Integer speakUserId, String speakUserName, String speakUserPicturePath, Integer otherUserId, String otherUserName, String otherUserPicturePath, List<ChatContent> chatContents) {
        this.speakUserId = speakUserId;
        this.speakUserName = speakUserName;
        this.speakUserPicturePath = speakUserPicturePath;
        this.otherUserId = otherUserId;
        this.otherUserName = otherUserName;
        this.otherUserPicturePath = otherUserPicturePath;
        this.chatContents = chatContents;
    }

    public Integer getSpeakUserId() {
        return speakUserId;
    }

    public void setSpeakUserId(Integer speakUserId) {
        this.speakUserId = speakUserId;
    }

    public String getSpeakUserName() {
        return speakUserName;
    }

    public void setSpeakUserName(String speakUserName) {
        this.speakUserName = speakUserName;
    }

    public String getSpeakUserPicturePath() {
        return speakUserPicturePath;
    }

    public void setSpeakUserPicturePath(String speakUserPicturePath) {
        this.speakUserPicturePath = speakUserPicturePath;
    }

    public Integer getOtherUserId() {
        return otherUserId;
    }

    public void setOtherUserId(Integer otherUserId) {
        this.otherUserId = otherUserId;
    }

    public String getOtherUserName() {
        return otherUserName;
    }

    public void setOtherUserName(String otherUserName) {
        this.otherUserName = otherUserName;
    }

    public String getOtherUserPicturePath() {
        return otherUserPicturePath;
    }

    public void setOtherUserPicturePath(String otherUserPicturePath) {
        this.otherUserPicturePath = otherUserPicturePath;
    }

    public List<ChatContent> getChatContents() {
        return chatContents;
    }

    public void setChatContents(List<ChatContent> chatContents) {
        this.chatContents = chatContents;
    }
}
