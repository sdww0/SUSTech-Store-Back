package com.susstore.pojo.chat;

import java.util.Date;

public class ChatHistory {

    private Integer dealId;
    private String otherUserName;
    private String otherUserPicturePath;
    private String lastMessageContent;
    private Date lastMessageDate;

    public ChatHistory() {
    }

    public ChatHistory(Integer dealId, String otherUserName, String otherUserPicturePath, String lastMessageContent, Date lastMessageDate) {
        this.dealId = dealId;
        this.otherUserName = otherUserName;
        this.otherUserPicturePath = otherUserPicturePath;
        this.lastMessageContent = lastMessageContent;
        this.lastMessageDate = lastMessageDate;
    }

    public Integer getDealId() {
        return dealId;
    }

    public void setDealId(Integer dealId) {
        this.dealId = dealId;
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

    public String getLastMessageContent() {
        return lastMessageContent;
    }

    public void setLastMessageContent(String lastMessageContent) {
        this.lastMessageContent = lastMessageContent;
    }

    public Date getLastMessageDate() {
        return lastMessageDate;
    }

    public void setLastMessageDate(Date lastMessageDate) {
        this.lastMessageDate = lastMessageDate;
    }
}
