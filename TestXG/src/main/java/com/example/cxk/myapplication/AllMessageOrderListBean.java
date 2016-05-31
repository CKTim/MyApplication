package com.example.cxk.myapplication;

import java.io.Serializable;
import java.util.List;

/**
 * Created by cxk on 2016/4/23.
 */
public class AllMessageOrderListBean implements Serializable {
    public int businessId;
    public int userId;
    public String email;
    public Double amount;
    public int payment;
    public String name;
    public String surname;
    public String districtAddress;
    public String roomAddress;
    public String tel;
    public int ordersId;
    public List<OrderListBean> orderitem;
    public String sign;
    public class OrderListBean{
       public String special;
        public int  num;
        public int  foodId;
   }

    public int getBusinessId() {
        return businessId;
    }

    public void setBusinessId(int businessId) {
        this.businessId = businessId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public int getPayment() {
        return payment;
    }

    public void setPayment(int payment) {
        this.payment = payment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getDistrictAddress() {
        return districtAddress;
    }

    public void setDistrictAddress(String districtAddress) {
        this.districtAddress = districtAddress;
    }

    public String getRoomAddress() {
        return roomAddress;
    }

    public void setRoomAddress(String roomAddress) {
        this.roomAddress = roomAddress;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public int getOrdersId() {
        return ordersId;
    }

    public void setOrdersId(int ordersId) {
        this.ordersId = ordersId;
    }

    public List<OrderListBean> getOrderitem() {
        return orderitem;
    }

    public void setOrderitem(List<OrderListBean> orderitem) {
        this.orderitem = orderitem;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
