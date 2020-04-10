package com.google.android.gms.samples.vision.barcodereader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryActivity extends Activity {
    private RecyclerView recyclerView;
    private HistoryAdapter adapter;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    TextView yourScore;
    String score;
    List<AnsweredQuestion> answeredQuestions = new ArrayList<>();
    List<Question> questionsAQ = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_activity);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view_QS);
        yourScore = (TextView) findViewById(R.id.yourScore);

        mAuth = FirebaseAuth.getInstance();

        getAnsweredQuestions();
    }

    public void populateRecyclerView(final List<Question> q) {
        Collections.sort(q, (o1, o2) -> o1.getCourse().compareTo(o2.getCourse()));
        adapter = new HistoryAdapter(q);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this.getApplicationContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Question selectedQuestion = q.get(position);
                        String key = selectedQuestion.getKey();
                        Intent intent = new Intent(HistoryActivity.this, SeeAQActivity.class);
                        intent.putExtra("KEY", key);
                        startActivity(intent);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                })
        );
    }

    public void getAnsweredQuestions() {
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        mDatabase.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
                for (HashMap.Entry i : map.entrySet()) {
                    if (i.getKey().equals("answers")) {
                        Map<String, Object> answersHM = (Map<String, Object>) i.getValue();
                        for (HashMap.Entry ii : answersHM.entrySet()) {
                            String key = (String) ii.getKey();
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
                            AnsweredQuestion aQ = new AnsweredQuestion(answer, correct);
                            aQ.setqId(key);
                            answeredQuestions.add(aQ);
                        }
                    }
                    if (i.getKey().equals("score")) {
                        score = (String) i.getValue();
                    }
                }
                getInfoAboutAQ();
                yourScore.setText(score);
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
                    for (AnsweredQuestion aQ : answeredQuestions) {
                        if (aQ.getqId().equals(d.getKey())) {
                            Question q = d.getValue(Question.class);
                            String key = d.getKey();
                            q.setKey(key);
                            String isCorrect;
                            if (aQ.isCorrect()) {
                                isCorrect = "correct";
                            } else {
                                isCorrect = "wrong";
                            }
                            q.setCorrect(isCorrect);
                            questionsAQ.add(q);
                        }
                    }
                }
                populateRecyclerView(questionsAQ);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
