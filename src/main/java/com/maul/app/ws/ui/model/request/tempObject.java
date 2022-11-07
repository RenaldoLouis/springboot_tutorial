package com.maul.app.ws.ui.model.request;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class tempObject {
    private long id;

    private String name;

    private String token;

    public tempObject(long id, String name, String token) {
        super();
        this.id = id;
        this.name = name;
        this.token = token;
    }

    public tempObject(String str) {
        super();
        List<String> list = new ArrayList<String>(Arrays.asList(str.split(" , ")));

        this.id = Long.parseLong(list.get(0));
        this.name = list.get(1);
        this.token = list.get(2);
    }

    public long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return id + " , " + name + " , " + token;
    }

}
