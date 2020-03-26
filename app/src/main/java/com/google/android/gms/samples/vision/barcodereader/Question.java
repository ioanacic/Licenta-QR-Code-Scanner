package com.google.android.gms.samples.vision.barcodereader;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Question {

    String key;
    String question, answerA, answerB, answerC, answerD;

    public Question() {

    }

    public Question(String question, String answerA, String answerB, String answerC, String answerD) {
        this.question = question;
        this.answerA = answerA;
        this.answerB = answerB;
        this.answerC = answerC;
        this.answerD = answerD;
    }

    public Question(String key, String question, String answerA, String answerB, String answerC, String answerD) {
        this.key = key;
        this.question = question;
        this.answerA = answerA;
        this.answerB = answerB;
        this.answerC = answerC;
        this.answerD = answerD;
    }

    public Question(Question q) {
        this.question = q.question;
        this.answerA = q.answerA;
        this.answerB = q.answerB;
        this.answerC = q.answerC;
        this.answerD = q.answerD;
    }

    public String getQuestion() { return question; }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswerA() {
        return answerA;
    }

    public void setAnswerA(String answerA) {
        this.answerA = answerA;
    }

    public String getAnswerB() {
        return answerB;
    }

    public void setAnswerB(String answerB) {
        this.answerB = answerB;
    }

    public String getAnswerC() {
        return answerC;
    }

    public void setAnswerC(String answerC) {
        this.answerC = answerC;
    }

    public String getAnswerD() {
        return answerD;
    }

    public void setAnswerD(String answerD) {
        this.answerD = answerD;
    }

    public String getKey() { return key; }

    public void setKey(String key) { this.key = key; }

    public static ArrayList<Question> createQuestionsList(int numberOfQuestions, Question q) {
        ArrayList<Question> questions = new ArrayList<Question>();

        for (int i = 0; i < numberOfQuestions; i++) {
            questions.add(new Question(q));
        }

        return questions;
    }
}
