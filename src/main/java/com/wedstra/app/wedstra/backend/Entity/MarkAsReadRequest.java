package com.wedstra.app.wedstra.backend.Entity;

import java.util.List;

public class MarkAsReadRequest {
    private List<String> messageIds;

    public List<String> getMessageIds() {
        return messageIds;
    }

    public void setMessageIds(List<String> messageIds) {
        this.messageIds = messageIds;
    }
}
