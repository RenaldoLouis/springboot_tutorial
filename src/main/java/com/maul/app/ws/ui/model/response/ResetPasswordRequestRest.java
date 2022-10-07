package com.maul.app.ws.ui.model.response;

import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.maul.app.ws.io.entity.UserEntity;

public class ResetPasswordRequestRest {
    private String token;

    @OneToOne()
    @JoinColumn(name = "users_id")
    private UserEntity userDetails; // we need userId which we will get from userEntity class

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserEntity getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserEntity userDetails) {
        this.userDetails = userDetails;
    }

}
