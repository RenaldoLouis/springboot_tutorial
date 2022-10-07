package com.maul.app.ws.io.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Entity(name = "password_reset_tokens") // nama database tablenya
public class PasswordResetTokenEntity implements Serializable {

    private static final long serialVersionUID = 8051324316462829780L;

    @Id
    @GeneratedValue
    private long id;

    private String token;

    @OneToOne() // one token only to one user
    @JoinColumn(name = "users_id") // akan ke join ke table users column id "${tableName_columnName}" userId jadi
                                   // foreigner key buat di table database password reset token
    private UserEntity userDetails; // we need userId which we will get from userEntity class

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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
