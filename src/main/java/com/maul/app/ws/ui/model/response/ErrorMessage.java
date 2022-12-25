package com.maul.app.ws.ui.model.response;

import java.util.Date;

public class ErrorMessage {
    private Date timestamp;
    private String Message;
    private Integer status;

    public ErrorMessage() {
    }

    public ErrorMessage(Date timestamp, String message, Integer status) {
        this.timestamp = timestamp;
        this.Message = message;
        this.status = status;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        this.Message = message;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
