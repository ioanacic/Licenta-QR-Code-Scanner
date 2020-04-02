package com.google.android.gms.samples.vision.barcodereader;

import java.util.ArrayList;
import java.util.List;

public class User {
    public String lastName, firstName, phone, group, yearOfStudy, email, password;
    public List<AnsweredQuestion> answers = new ArrayList<AnsweredQuestion>();
    public String score = "0";

    public User() {

    }

    public User(String lastName, String firstName, String phone, String group, String yearOfStudy, String email, String password, String score) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.phone = phone;
        this.group = group;
        this.yearOfStudy = yearOfStudy;
        this.email = email;
        this.password = password;
    }

    public User(String lastName, String firstName, String phone, String group, String yearOfStudy, String email,
                String password, List<AnsweredQuestion> answers, String score) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.phone = phone;
        this.group = group;
        this.yearOfStudy = yearOfStudy;
        this.email = email;
        this.password = password;
        this.answers = answers;
        this.score = score;
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

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getYearOfStudy() {
        return yearOfStudy;
    }

    public void setYearOfStudy(String yearOfStudy) {
        this.yearOfStudy = yearOfStudy;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<AnsweredQuestion> getAnswers() {
        return answers;
    }

    public void setAnswers(List<AnsweredQuestion> answers) {
        this.answers = answers;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

}
