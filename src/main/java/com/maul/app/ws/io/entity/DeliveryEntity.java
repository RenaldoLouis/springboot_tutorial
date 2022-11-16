package com.maul.app.ws.io.entity;

import java.sql.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "delivery")
public class DeliveryEntity {

    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false, length = 25)
    private String name;

    @Column(nullable = false, length = 15)
    private int quantity;

    @Column(nullable = false, length = 50)
    private String deliveryCode;

    @Column(nullable = false)
    private Date deliveryTime;

    @Column(nullable = false)
    private String buyerId;

    @Column(nullable = false)
    private boolean completed;

    @OneToOne(cascade = {
            CascadeType.PERSIST }, fetch = FetchType.EAGER, targetEntity = com.maul.app.ws.io.entity.CourierEntity.class)
    @JoinTable(name = "delivery_courier", joinColumns = @JoinColumn(name = "delivery_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "courier_id", referencedColumnName = "id"))
    private CourierEntity courier;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDeliveryCode() {
        return deliveryCode;
    }

    public void setDeliveryCode(String deliveryCode) {
        this.deliveryCode = deliveryCode;
    }

    public Date getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(Date deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public CourierEntity getCourier() {
        return courier;
    }

    public void setCourier(CourierEntity courier) {
        this.courier = courier;
    }
}
