package com.google.android.gms.samples.vision.barcodereader;

public class AnsweredQuestion {
    String qId, answer;
    boolean isCorrect;

    String subject;

    public AnsweredQuestion() {

    }

    public AnsweredQuestion(String qId) {
        this.qId = qId;
    }

    public AnsweredQuestion(String answer, boolean isCorrect) {
        this.answer = answer;
        this.isCorrect = isCorrect;
    }

    public AnsweredQuestion(String qId, String answer, boolean isCorrect) {
        this.qId = qId;
        this.answer = answer;
        this.isCorrect = isCorrect;
    }

    public AnsweredQuestion(AnsweredQuestion aq) {
        this.qId = aq.qId;
        this.answer = aq.answer;
        this.isCorrect = aq.isCorrect;
    }

    public String getqId() {
        return qId;
    }

    public void setqId(String qId) {
        this.qId = qId;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
