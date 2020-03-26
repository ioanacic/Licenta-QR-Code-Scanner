package com.google.android.gms.samples.vision.barcodereader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class SeeQuestionsActivity extends Activity {
    private RecyclerView recyclerView;

    List<Question> questions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.see_questions_activity);

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        Question testQ1 = new Question("Q1", "A", "B", "C", "D");
        Question testQ2 = new Question("Q2", "A", "B", "C", "D");
        Question testQ3 = new Question("Q3", "A", "B", "C", "D");
        questions.add(testQ1);
        questions.add(testQ2);
        questions.add(testQ3);

        QuestionsAdapter adapter = new QuestionsAdapter(questions);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this.getApplicationContext(), recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Intent intent = new Intent(SeeQuestionsActivity.this, GenerateQRActivity.class);
                        startActivity(intent);
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );
    }
}
