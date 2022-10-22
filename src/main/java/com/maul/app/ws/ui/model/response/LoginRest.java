package com.maul.app.ws.ui.model.response;

public class LoginRest {
    private String token;
    private String userID;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
