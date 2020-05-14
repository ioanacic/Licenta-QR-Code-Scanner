package com.google.android.gms.samples.vision.barcodereader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

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

public class SeeTestsActivity extends Activity implements View.OnClickListener {
    private RecyclerView recyclerView;
    private TestsAdapter adapter;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    Spinner spinner;

    List<Test> tests = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.see_tests_activity);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view_tests);

        spinner = (Spinner) findViewById(R.id.subjectsOptionsTests);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                onOptionSelected();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("tests");

        populateRecyclerView();
        getData();
    }

    // ca in SeeQuestionsActivity
    public void populateRecyclerView() {
        // sort alfabetically
        adapter = new TestsAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this.getApplicationContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // TODO save in specific folders like QR s for questions
                        Test selectedTest = adapter.getMyTests().get(position);
                        String key = selectedTest.getKey();
                        Intent intent = new Intent(SeeTestsActivity.this, GenerateQRActivity.class);
                        intent.putExtra("KEY", key);
                        startActivity(intent);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                })
        );
    }

    public void getData() {
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();

                for (HashMap.Entry i : map.entrySet()) {
                    Map<String, Object> oneTest = (HashMap<String, Object>) i.getValue();

                    String testKey = i.getKey().toString();
                    int numberOfQuestions = 0;
                    String title = "", professorKey = "";
                    List<String> qs = new ArrayList<>();

                    for (HashMap.Entry ii : oneTest.entrySet()) {
                        if (ii.getKey().equals("numberOfQuestions")) {
                            numberOfQuestions = Integer.valueOf(String.valueOf(ii.getValue()));
                        } else if (ii.getKey().equals("title")) {
                            title = (String) ii.getValue();
                        } else if (ii.getKey().equals("professorKey")) {
                            professorKey = (String) ii.getValue();
                        } else {
                            qs.add((String) ii.getKey());
                        }
                    }

                    Test t = new Test(testKey, professorKey, title, numberOfQuestions);
                    t.setQuestionsId(qs);

                    tests.add(t);
                }

                adapter.notifyDataSetChanged();
                adapter.updateT(tests);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View view) {

    }
}
