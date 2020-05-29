package com.google.android.gms.samples.vision.barcodereader;

import java.util.ArrayList;
import java.util.List;

public class User {
    public String key;
    public String lastName, firstName, phone, email, password;
    public String typeOfUser;       // S = Student, P = Professor

    public User() {

    }

    public User(String lastName, String firstName) {
        this.lastName = lastName;
        this.firstName = firstName;
    }

    public User(String lastName, String firstName, String typeOfUser) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.typeOfUser = typeOfUser;
    }

    public User(String lastName, String firstName, String phone, String email, String typeOfUser) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.phone = phone;
        this.email = email;
        this.typeOfUser = typeOfUser;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTypeOfUser() {
        return typeOfUser;
    }

    public void setTypeOfUser(String typeOfUser) {
        this.typeOfUser = typeOfUser;
    }
}
