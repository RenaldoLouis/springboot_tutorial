package com.maul.app.ws.ui.model.request;

public class CourierRequestModel {
    private String name;

    private boolean vacant;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isVacant() {
        return vacant;
    }

    public void setVacant(boolean vacant) {
        this.vacant = vacant;
    }
}
