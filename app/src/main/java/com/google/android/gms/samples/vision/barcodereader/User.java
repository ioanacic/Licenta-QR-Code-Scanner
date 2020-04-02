package com.google.android.gms.samples.vision.barcodereader;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public String getLastName() { return lastName; }

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

    public List<AnsweredQuestion> getAnswers() { return answers; }

    public void setAnswers(HashMap<String, HashMap<String, HashMap<String, HashMap<String, String>>>> answers) {
        HashMap<String, HashMap<String, HashMap<String, String>>> questions =  answers.get("answers");       // get all questions from key = answers
        Log.d("", answers.toString() );
        for (HashMap.Entry i : questions.entrySet()) {
            AnsweredQuestion aQ = new AnsweredQuestion();
            aQ.setqId(i.getKey().toString());
            HashMap<String, HashMap<String, String>> valuesAQ = (HashMap<String, HashMap<String, String>>) i.getValue();
            Log.d("", valuesAQ.toString() );

            for (HashMap.Entry ii: valuesAQ.entrySet()) {
                HashMap<String, String> test = (HashMap<String, String>) ii.getValue();
                Log.d("", test.toString() );
                aQ.setAnswer(test.get("answer"));
                aQ.setCorrect(Boolean.parseBoolean(test.get("correct")));
            }
//            aQ.setAnswer(valuesAQ.get("answer"));
//            aQ.setCorrect(Boolean.parseBoolean(valuesAQ.get("correct")));

            this.answers.add(aQ);

        }
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

}
