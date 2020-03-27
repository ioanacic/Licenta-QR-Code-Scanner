package com.google.android.gms.samples.vision.barcodereader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SeeQuestionsActivity extends Activity {
    private RecyclerView recyclerView;
    private DatabaseReference mDatabase;
    private QuestionsAdapter adapter;

    List<Question> questions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.see_questions_activity);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        getData();

        adapter = new QuestionsAdapter(questions);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this.getApplicationContext(), recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Question selectedQuestion = questions.get(position);
                        String key = selectedQuestion.getKey();
                        Intent intent = new Intent(SeeQuestionsActivity.this, GenerateQRActivity.class);
                        intent.putExtra("KEY", key);
                        startActivity(intent);
                    }

                    @Override public void onLongItemClick(View view, int position) {

                    }
                })
        );
    }

    public void getData() {
        mDatabase = FirebaseDatabase.getInstance().getReference("questions");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    Question q = d.getValue(Question.class);
                    String key = d.getKey();
                    q.setKey(key);
                    questions.add(q);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
