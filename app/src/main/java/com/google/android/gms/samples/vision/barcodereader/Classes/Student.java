package com.google.android.gms.samples.vision.barcodereader.Classes;

import java.util.ArrayList;
import java.util.List;

public class Student extends User {
    public String group, yearOfStudy;
    public List<AnsweredQuestion> answers = new ArrayList<AnsweredQuestion>();

    public Student() {

    }

    public Student(String group, String yearOfStudy, List<AnsweredQuestion> answers) {
        this.group = group;
        this.yearOfStudy = yearOfStudy;
        this.answers = answers;
    }

    public Student(String lastName, String firstName, String group, String yearOfStudy) {
        super(lastName, firstName);
        this.group = group;
        this.yearOfStudy = yearOfStudy;
    }

    public Student(String lastName, String firstName, String typeOfUser, String group, String yearOfStudy, List<AnsweredQuestion> answers) {
        super(lastName, firstName, typeOfUser);
        this.group = group;
        this.yearOfStudy = yearOfStudy;
        this.answers = answers;
    }

    public Student(String lastName, String firstName, String phone, String email, String typeOfUser,
                   String group, String yearOfStudy, List<AnsweredQuestion> answers) {
        super(lastName, firstName, phone, email, typeOfUser);
        this.group = group;
        this.yearOfStudy = yearOfStudy;
        this.answers = answers;
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

    public List<AnsweredQuestion> getAnswers() {
        return answers;
    }

    public void setAnswers(List<AnsweredQuestion> answers) {
        this.answers = answers;
    }
}
