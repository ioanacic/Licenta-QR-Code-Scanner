package com.google.android.gms.samples.vision.barcodereader;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class SeeAQFragment extends Fragment {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase, questionsAsProfessor, getQuestionsAsStudent;

    TextView questionText, infoMessage, statistics;
    RadioButton answerA, answerB, answerC, answerD;
    RadioGroup answersGr;

    String keyOfSelectedQuestion, keyOfSelectedStudent, selectedAnswer, correctAnswer;
    Question question;
    AnsweredQuestion answeredQuestion;

    Double countTotal = 0.0;         // total of students who answered to this question
    Double countCorrect = 0.0;       // total of student to answered correctly to this question

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.see_aq_activity, container, false);

        questionText = (TextView) rootView.findViewById(R.id.questionText);
        answerA = (RadioButton) rootView.findViewById(R.id.answerA);
        answerB = (RadioButton) rootView.findViewById(R.id.answerB);
        answerC = (RadioButton) rootView.findViewById(R.id.answerC);
        answerD = (RadioButton) rootView.findViewById(R.id.answerD);
        answersGr = (RadioGroup) rootView.findViewById(R.id.answersGroup);
        infoMessage = (TextView) rootView.findViewById(R.id.infoMessage);
        statistics = (TextView) rootView.findViewById(R.id.statistics);

        mAuth = FirebaseAuth.getInstance();

        getInfoAboutAQ();

        return rootView;
    }

    public void setKeys(String keyOfSelectedQuestion, String keyOfSelectedStudent) {
        this.keyOfSelectedQuestion = keyOfSelectedQuestion;
        this.keyOfSelectedStudent = keyOfSelectedStudent;
    }

    public void getAnsweredQuestion() {
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        if (keyOfSelectedStudent != null) {
            // the professor wants to see the student
            questionsAsProfessor = mDatabase.child(keyOfSelectedStudent);
            seeAnsweredQuestion(questionsAsProfessor);
        } else {
            // the student wants to see himself
            getQuestionsAsStudent = mDatabase.child(mAuth.getCurrentUser().getUid());
            seeAnsweredQuestion(getQuestionsAsStudent);
        }
    }

    public void seeAnsweredQuestion(DatabaseReference dbR) {
        dbR.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
                for (HashMap.Entry i : map.entrySet()) {
                    if (i.getKey().equals("answers")) {
                        Map<String, Object> answersHM = (Map<String, Object>) i.getValue();
                        for (HashMap.Entry ii : answersHM.entrySet()) {
                            if (ii.getKey().equals(keyOfSelectedQuestion)) {
                                Map<String, Map<String, Object>> oneAnswer = (Map<String, Map<String, Object>>) ii.getValue();
                                String answer = "";
                                boolean correct = false;
                                for (HashMap.Entry oneField : oneAnswer.entrySet()) {
                                    if (oneField.getKey().equals("answer")) {
                                        answer = (String) oneField.getValue();
                                    }
                                    if (oneField.getKey().equals("correct")) {
                                        correct = (boolean) oneField.getValue();
                                    }
                                }
                                selectedAnswer = answer;
                                answeredQuestion = new AnsweredQuestion(answer, correct);

                                setSelectedAnswer();

                                answerA.setVisibility(View.VISIBLE);
                                answerB.setVisibility(View.VISIBLE);
                                answerC.setVisibility(View.VISIBLE);
                                answerD.setVisibility(View.VISIBLE);

                                changeColors(answerA);
                                changeColors(answerB);
                                changeColors(answerC);
                                changeColors(answerD);
                            }
                        }
                    }
                }

                getStatistics();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getInfoAboutAQ() {
        mDatabase = FirebaseDatabase.getInstance().getReference("questions");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    if (d.getKey().equals(keyOfSelectedQuestion)) {
                        question = d.getValue(Question.class);
                        questionText.setText(question.getQuestion());
                        answerA.setText(question.getAnswerA());
                        answerB.setText(question.getAnswerB());
                        answerC.setText(question.getAnswerC());
                        answerD.setText(question.getAnswerD());
                        correctAnswer = question.getCorrectAnswer();
                        correctAnswer = format(correctAnswer);
                    }
                }
                getAnsweredQuestion();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void changeColors(RadioButton button) {
        if (selectedAnswer.equals(button.getTag().toString()) && selectedAnswer.equals(correctAnswer)) {
            button.setTextColor(Color.parseColor("#008f00"));
        }
        if (selectedAnswer.equals(button.getTag().toString()) && !selectedAnswer.equals(correctAnswer)) {
            button.setTextColor(Color.parseColor("#E8000D"));
        }
        if (correctAnswer.equals(button.getTag().toString()) && !selectedAnswer.equals(correctAnswer)) {
            button.setTextColor(Color.parseColor("#008f00"));
        }
    }

    public void setSelectedAnswer() {
        switch (selectedAnswer) {
            case "-1":
                answerA.setEnabled(false);
                answerB.setEnabled(false);
                answerC.setEnabled(false);
                answerD.setEnabled(false);
                infoMessage.setText(R.string.noSubmission);
                break;
            case "answerA":
                answersGr.check(R.id.answerA);
                answerB.setEnabled(false);
                answerC.setEnabled(false);
                answerD.setEnabled(false);
                break;
            case "answerB":
                answersGr.check(R.id.answerB);
                answerA.setEnabled(false);
                answerC.setEnabled(false);
                answerD.setEnabled(false);
                break;
            case "answerC":
                answersGr.check(R.id.answerC);
                answerA.setEnabled(false);
                answerB.setEnabled(false);
                answerD.setEnabled(false);
                break;
            case "answerD":
                answersGr.check(R.id.answerD);
                answerA.setEnabled(false);
                answerB.setEnabled(false);
                answerC.setEnabled(false);
                break;
        }
    }

    public String format(String s) {
        String sFormatted = null;
        switch (s) {
            case "Answer A":
                sFormatted = "answerA";
                break;
            case "Answer B":
                sFormatted = "answerB";
                break;
            case "Answer C":
                sFormatted = "answerC";
                break;
            case "Answer D":
                sFormatted = "answerD";
                break;
        }

        return sFormatted;
    }

    public void getStatistics() {
        FirebaseDatabase.getInstance().getReference("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()) {

                    // get the type of user
                    Map<String, User> userValue = (HashMap<String, User>) d.getValue();
                    String typeOfUser = "";
                    for (HashMap.Entry i : userValue.entrySet()) {
                        if (i.getKey().equals("typeOfUser")) {
                            typeOfUser = (String) i.getValue();
                        }
                    }

                    // we only get the students
                    if (typeOfUser.equals("S")) {
                        for (HashMap.Entry i : userValue.entrySet()) {
                            if (i.getKey().equals("answers")) {
                                Map<String, Object> answersHM = (Map<String, Object>) i.getValue();
                                for (HashMap.Entry ii : answersHM.entrySet()) {
                                    if (ii.getKey().equals(keyOfSelectedQuestion)) {
                                        countTotal++;

                                        Map<String, Map<String, Object>> oneAnswer = (Map<String, Map<String, Object>>) ii.getValue();
                                        for (HashMap.Entry oneField : oneAnswer.entrySet()) {
                                            if (oneField.getKey().equals("correct")) {
                                                if ((boolean) oneField.getValue()) {
                                                    countCorrect++;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Double result = countCorrect/countTotal*100;
                String resultStr = result.toString();
                if (resultStr.length() >= 5) {
                    resultStr = resultStr.substring(0, 5);
                }
                statistics.setText("A total of " + resultStr + "% answered correctly to this question");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
