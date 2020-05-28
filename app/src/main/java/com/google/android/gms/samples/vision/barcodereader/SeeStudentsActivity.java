package com.google.android.gms.samples.vision.barcodereader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SeeStudentsActivity extends Activity {
    private RecyclerView recyclerView;
    private StudentsAdapter adapter;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    Spinner spinner;
    Button saveFile;

    List<Student> students = new ArrayList<>();
    List<Question> questions = new ArrayList<>();
    List<Student> selectedStudents = new ArrayList<>();
    Map<String, String> scorePerSubject = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.see_students_activity);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view_students);

        spinner = (Spinner) findViewById(R.id.groupsOptions);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                onOptionSelected();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        saveFile = findViewById(R.id.saveFile);
        saveFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveCsvFile();
                Toast.makeText(SeeStudentsActivity.this, R.string.csvFileSaved, Toast.LENGTH_SHORT).show();
            }
        });

        mAuth = FirebaseAuth.getInstance();

        getAllQuestions();
        populateRecyclerView();

    }

    public void populateRecyclerView() {
        adapter = new StudentsAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this.getApplicationContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Student selectedStudent = adapter.getMyStudents().get(position);
                        String key = selectedStudent.getKey();
                        Intent intent = new Intent(SeeStudentsActivity.this, HistoryActivity.class);
                        intent.putExtra("KEY", key);
                        startActivity(intent);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                })
        );
    }

    public void getStudents() {
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
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
                        String key = d.getKey();
                        Map<String, Student> studentValue = (HashMap<String, Student>) d.getValue();
                        String lastName = "";
                        String firstName = "";
                        String group = "";
                        String year = "";
                        // create the list of answered question of this student
                        List<AnsweredQuestion> aqList = new ArrayList<>();

                        for (HashMap.Entry i : studentValue.entrySet()) {
                            if (i.getKey().equals("lastName")) {
                                lastName = (String) i.getValue();
                            }
                            if (i.getKey().equals("firstName")) {
                                firstName = (String) i.getValue();
                            }
                            if (i.getKey().equals("group")) {
                                group = (String) i.getValue();
                            }
                            if (i.getKey().equals("yearOfStudy")) {
                                year = (String) i.getValue();
                            }
                            if (i.getKey().equals("answers")) {
                                Map<String, Object> answersHM = (Map<String, Object>) i.getValue();
                                for (HashMap.Entry ii : answersHM.entrySet()) {
                                    String aqKey = (String) ii.getKey();
                                    AnsweredQuestion aQ = new AnsweredQuestion(aqKey);

                                    Map<String, Object> oneAnswer = (Map<String, Object>) ii.getValue();
                                    for (HashMap.Entry oneField : oneAnswer.entrySet()) {
                                        if (oneField.getKey().equals("correct")) {
                                            aQ.setCorrect((boolean) oneField.getValue());
                                        }
                                    }
                                    aqList.add(aQ);
                                }
                            }

                        }
                        if (aqList.size() != 0) {
                            checkQuestions(aqList, lastName, firstName, group, year, key);
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void addStudent(List<AnsweredQuestion> aqList, String lastName, String firstName, String group, String year, String key) {
        boolean exists = false;

        // if there isn't already another student with the same key
        if (students.size() != 0) {
            for (Student st : students) {
                if (st.getKey().equals(key)) {
                    exists = true;          // there is a student with this key
                    break;
                }
            }
        }

        // if doesn't exist
        if (!exists) {
            Student s = new Student(lastName, firstName, group, year);
            s.setKey(key);
            s.setAnswers(aqList);
            students.add(s);
        }
    }

    public void checkQuestions(List<AnsweredQuestion> aqList, String lastName, String firstName, String group, String year, String key) {
        FirebaseDatabase.getInstance().getReference("questions").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<AnsweredQuestion> aqListForProfessor = new ArrayList<>();

                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    for (AnsweredQuestion aq : aqList) {
                        if (d.getKey().equals(aq.getqId())) {
                            Question q = d.getValue(Question.class);

                            // check who created the question
                            if (mAuth.getCurrentUser().getUid().equals(q.getIdProfessor())) {
                                aq.setSubject(q.getSubject());
                                aqListForProfessor.add(aq);
                            }
                        }
                    }
                }
                if (!aqListForProfessor.isEmpty()) {
                    addStudent(aqListForProfessor, lastName, firstName, group, year, key);
                }
                adapter.notifyDataSetChanged();
                addItemOnSpinner();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getAllQuestions() {
        FirebaseDatabase.getInstance().getReference("questions").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    Question q = d.getValue(Question.class);
                    questions.add(q);
                }
                getStudents();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void addItemOnSpinner() {
        List<String> options = new ArrayList<String>();
        options.add("All groups");

        for (Student s : students) {
            boolean contains = false;
            for (String o : options) {
                if (s.getGroup().equals(o)) {
                    contains = true;
                }
            }
            if (contains == false) {
                options.add(s.getGroup());
            }
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, options);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(dataAdapter);
    }

    public void onOptionSelected() {
        selectedStudents.clear();
        String selectedOption = spinner.getSelectedItem().toString().trim();

        for (Student s : students) {
            if (s.getGroup().equals(selectedOption)) {
                selectedStudents.add(s);
            }
        }

        if (spinner.getSelectedItem().toString().trim().equals("All groups")) {
            adapter.updateS(students);
        } else {
            adapter.updateS(selectedStudents);
        }
    }

    public List<String> getAllSubjects() {
        List<String> subjects = new ArrayList<>();

        for (Student st : selectedStudents) {
            List<AnsweredQuestion> aqList = st.getAnswers();        // the list of answered questions for this student

            // get the subjects
            for (AnsweredQuestion aq : aqList) {
                boolean contains = false;
                for (String s : subjects) {
                    if (aq.getSubject().equals(s)) {
                        contains = true;
                    }
                }
                if (contains == false) {
                    subjects.add(aq.getSubject());
                    scorePerSubject.put(aq.getSubject(), "0.0");
                }
            }
        }

        return subjects;
    }

    public void saveCsvFile() {
        // HOW IT S DONE - poti salva doar daca a selectat o grupa, iar pt o grupa salveaza scorurile pentru fiecare materie
        String groupNumber = spinner.getSelectedItem().toString().trim();
        if (groupNumber.equals("All groups")) {
            Toast.makeText(SeeStudentsActivity.this, R.string.selectAGroup, Toast.LENGTH_SHORT).show();
            return;
        }

        File filepath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File dir;
        String addToPath = "/Quiz Results/";
        dir = new File(filepath.getAbsoluteFile() + addToPath);
        dir.mkdir();

        final String filename = dir.toString() + "/" + groupNumber + ".csv";

        List<String> subjects = getAllSubjects();

        new Thread() {
            public void run() {
                try {

                    FileWriter fw = new FileWriter(filename);

                    fw.append("Nr. crt.");
                    fw.append(';');

                    fw.append("Nume complet");
                    fw.append(';');

                    for (String s : subjects) {
                        fw.append(s);
                        fw.append(';');
                    }


                    fw.append('\n');

                    Integer i = 1;
                    for (Student st : selectedStudents) {
                        fw.append(i.toString());
                        fw.append(';');

                        fw.append(st.getLastName() + " " + st.getFirstName());
                        fw.append(';');

                        List<AnsweredQuestion> aqList = st.getAnswers();

                        for (AnsweredQuestion aq : aqList) {
                            for (String s : subjects) {
                                if (aq.getSubject().equals(s)) {
                                    if (aq.isCorrect()) {
                                        String score = scorePerSubject.get(s);
                                        Double scoreDouble = Double.parseDouble(score);
                                        scoreDouble += 0.1;

                                        score = scoreDouble.toString();
                                        score = score.substring(0, 3);

                                        scorePerSubject.put(s, score);
                                    }
                                }
                            }
                        }

                        i++;

                        for (Map.Entry entry : scorePerSubject.entrySet()) {
                            fw.append(entry.getValue().toString());
                            fw.append(';');
                        }

                        fw.append('\n');
                    }

                    // fw.flush();
                    fw.close();


                    scorePerSubject.clear();

                } catch (Exception e) {
                }
            }
        }.start();

    }
}
