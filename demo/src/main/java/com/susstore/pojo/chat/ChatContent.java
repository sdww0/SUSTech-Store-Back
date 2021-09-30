package com.susstore.pojo.chat;

import java.util.Date;

public class ChatContent {


    private Boolean isSpeakUser;
    private String content;
    private Date date;

    public ChatContent() {
    }

    public Boolean getSpeakUser() {
        return isSpeakUser;
    }

    public void setSpeakUser(Boolean speakUser) {
        isSpeakUser = speakUser;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
