package com.example.foodcraze.Model;

public class User {
    private String name;
    private String password;
    private String uid,address,phone;

    public User() {
    }

    public User(String name, String password,String uid,String address,String phone) {
        this.name = name;
        this.password = password;
        this.address = address;
        this.uid = uid;
        this.phone = phone;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
