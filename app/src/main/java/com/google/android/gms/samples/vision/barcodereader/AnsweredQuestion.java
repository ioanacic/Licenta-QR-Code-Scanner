package com.google.android.gms.samples.vision.barcodereader;

public class AnsweredQuestion {
    String qId, answer;
    boolean isCorrect;

    public AnsweredQuestion() {

    }

    public AnsweredQuestion(String answer, boolean isCorrect) {
        this.answer = answer;
        this.isCorrect = isCorrect;
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
}
