package com.google.android.gms.samples.vision.barcodereader;

public class Question {

    String key;
    String course, question, answerA, answerB, answerC, answerD, correctAnswer;
    String isCorrect;
    String idProfessor;
    String subject;

    boolean isQrGenerated = false;

    // for "Add question to test"
    boolean isSelected = false;    // has not been selected
                                   // false = buton gol, true = buton plin

    public Question() {

    }

    public Question(String question, String course, String answerA, String answerB, String answerC, String answerD,
                    String correctAnswer, String idProfessor, String subject) {
        this.question = question;
        this.course = course;
        this.answerA = answerA;
        this.answerB = answerB;
        this.answerC = answerC;
        this.answerD = answerD;
        this.correctAnswer = correctAnswer;
        this.idProfessor = idProfessor;
        this.subject = subject;

        isSelected = false;
        isQrGenerated = false;
    }

    public Question(String key, String question, String course, String answerA, String answerB, String answerC, String answerD,
                    String correctAnswer, String idProfessor, String subject) {
        this.key = key;
        this.question = question;
        this.course = course;
        this.answerA = answerA;
        this.answerB = answerB;
        this.answerC = answerC;
        this.answerD = answerD;
        this.correctAnswer = correctAnswer;
        this.idProfessor = idProfessor;
        this.subject = subject;
    }

    public Question(Question q) {
        this.question = q.question;
        this.course = q.course;
        this.answerA = q.answerA;
        this.answerB = q.answerB;
        this.answerC = q.answerC;
        this.answerD = q.answerD;
        this.correctAnswer = q.correctAnswer;
        this.subject = q.subject;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
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

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String isCorrect() {
        return isCorrect;
    }

    public void setCorrect(String isCorrect) {
        this.isCorrect = isCorrect;
    }

    public String getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(String isCorrect) {
        this.isCorrect = isCorrect;
    }

    public String getIdProfessor() {
        return idProfessor;
    }

    public void setIdProfessor(String idProfessor) {
        this.idProfessor = idProfessor;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean getIsQrGenerated() {
        return isQrGenerated;
    }

    public void setIsQrGenerated(boolean isQrGenerated) {
        this.isQrGenerated = isQrGenerated;
    }
}
