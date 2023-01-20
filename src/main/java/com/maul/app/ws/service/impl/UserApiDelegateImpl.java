package com.maul.app.ws.service.impl;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.maul.app.ws.service.api.UsersApiDelegate;
import com.maul.app.ws.service.model.User;

@Service
public class UserApiDelegateImpl implements UsersApiDelegate {

    @Override
    public ResponseEntity<User> getUserByid(String id) {
        User user = new User();
        user.setUserId("123L");
        user.setFirstName("Petros");

        // ... omit other initialization

        return ResponseEntity.ok(user);
    }

}
