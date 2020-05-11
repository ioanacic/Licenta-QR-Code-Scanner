package com.google.android.gms.samples.vision.barcodereader;

import java.util.ArrayList;
import java.util.List;

public class Professor extends User {
    public List<String> subjects = new ArrayList<String>();

    Professor() {

    }

    public Professor(List<String> subjects) {
        this.subjects = subjects;
    }

    public Professor(String lastName, String firstName, List<String> subjects) {
        super(lastName, firstName);
        this.subjects = subjects;
    }

    public Professor(String lastName, String firstName, String typeOfUser, List<String> subjects) {
        super(lastName, firstName, typeOfUser);
        this.subjects = subjects;
    }

    public Professor(String lastName, String firstName, String phone, String email, String password,  String typeOfUser, List<String> subjects) {
        super(lastName, firstName, phone, email, password, typeOfUser);
        this.subjects = subjects;
    }

    public List<String> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<String> subjects) {
        this.subjects = subjects;
    }
}
