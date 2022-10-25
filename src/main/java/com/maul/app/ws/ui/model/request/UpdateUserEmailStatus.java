package com.maul.app.ws.ui.model.request;

public class UpdateUserEmailStatus {
    private String userId;
    private Boolean status;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
