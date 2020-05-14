package com.google.android.gms.samples.vision.barcodereader;

import java.util.ArrayList;
import java.util.List;

public class Test {
    String key;
    String professorKey, title;
    List<String> questionsId;
    int numberOfQuestions;

    public Test() {

    }

    public Test(String professorKey, String title, int numberOfQuestions) {
        this.professorKey = professorKey;
        this.title = title;
        this.numberOfQuestions = numberOfQuestions;

        questionsId = new ArrayList<>();
    }

    public Test(String key, String professorKey, String title, int numberOfQuestions) {
        this.key = key;
        this.professorKey = professorKey;
        this.title = title;
        this.numberOfQuestions = numberOfQuestions;
    }

    public String getProfessorKey() {
        return professorKey;
    }

    public void setProfessorKey(String professorKey) {
        this.professorKey = professorKey;
    }

    public int getNumberOfQuestions() {
        return numberOfQuestions;
    }

    public void setNumberOfQuestions(int numberOfQuestions) {
        this.numberOfQuestions = numberOfQuestions;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<String> getQuestionsId() {
        return questionsId;
    }

    public void setQuestionsId(List<String> questionsId) {
        this.questionsId = questionsId;
    }
}
