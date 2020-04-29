package com.google.android.gms.samples.vision.barcodereader;

public class Professor extends User {
    public String subject;

    Professor() {

    }

    public Professor(String subject) {
        this.subject = subject;
    }

    public Professor(String lastName, String firstName, String typeOfUser, String subject) {
        super(lastName, firstName, typeOfUser);
        this.subject = subject;
    }

    public Professor(String lastName, String firstName, String phone, String email, String password,  String typeOfUser, String subject) {
        super(lastName, firstName, phone, email, password, typeOfUser);
        this.subject = subject;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
