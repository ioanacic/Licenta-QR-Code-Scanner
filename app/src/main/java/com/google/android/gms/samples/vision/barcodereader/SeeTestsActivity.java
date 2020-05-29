package com.google.android.gms.samples.vision.barcodereader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
    List<Test> selectedTests = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.see_tests_activity);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view_tests);

        spinner = (Spinner) findViewById(R.id.subjectsOptionsTests);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                onOptionSelectedSubject();
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
                        Test selectedTest = adapter.getMyTests().get(position);
                        String key = selectedTest.getKey();
                        Intent intent = new Intent(SeeTestsActivity.this, GenerateQRActivity.class);
                        intent.putExtra("KEY", key);
                        intent.putExtra("TYPE", "test");
                        startActivity(intent);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        Test selectedTest = adapter.getMyTests().get(position);
                        String key = selectedTest.getKey();
                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        //Yes button clicked

                                        mDatabase.child(key).removeValue();
                                        Intent intent = new Intent(SeeTestsActivity.this, SeeTestsActivity.class);
                                        startActivity(intent);
                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        //No button clicked
                                        break;
                                }
                            }
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                        builder.setMessage("Are you sure you want to permanently delete the test?").
                                setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
                        adapter.notifyDataSetChanged();
                    }
                })
        );
    }

    public void getData() {
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
                // TODO only the tests for logged professor

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
                addItemOnSpinner();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getSubject() {
        for (Test t : tests) {
            if (t.getTitle().contains("-")) {
                t.setSubject(t.getTitle().substring(0, t.getTitle().indexOf(" - ")));
            } else {
                t.setSubject(t.getTitle());
            }
        }
    }

    public void addItemOnSpinner() {
        getSubject();

        List<String> options = new ArrayList<String>();
        options.add("All subjects");

        for (Test t : tests) {
            boolean contains = false;
            for (String o : options) {
                if (t.getSubject().equals(o)) {
                    contains = true;
                }
            }
            if (contains == false) {
                options.add(t.getSubject());
            }
        }

        Collections.sort(options, (o1, o2) -> o1.compareTo(o2));

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, options);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(dataAdapter);
    }

    public void onOptionSelectedSubject() {
        selectedTests.clear();
        String selectedOption = spinner.getSelectedItem().toString().trim();

        for (Test t : tests) {
            if (t.getSubject().equals(selectedOption)) {
                selectedTests.add(t);
            }
        }

        if (spinner.getSelectedItem().toString().trim().equals("All subjects")) {
            adapter.updateT(tests);
        } else {
            adapter.updateT(selectedTests);

        }
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(this, ProfessorAccountActivity.class);
        startActivity(intent);
    }
}
