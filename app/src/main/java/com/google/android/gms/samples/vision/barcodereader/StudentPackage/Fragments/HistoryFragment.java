package com.google.android.gms.samples.vision.barcodereader.StudentPackage.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.samples.vision.barcodereader.Classes.AnsweredQuestion;
import com.google.android.gms.samples.vision.barcodereader.Adapters.HistoryAdapter;
import com.google.android.gms.samples.vision.barcodereader.Classes.Professor;
import com.google.android.gms.samples.vision.barcodereader.ProfessorPackage.ProfessorMenuActivity;
import com.google.android.gms.samples.vision.barcodereader.Question;
import com.google.android.gms.samples.vision.barcodereader.R;
import com.google.android.gms.samples.vision.barcodereader.RecyclerItemClickListener;
import com.google.android.gms.samples.vision.barcodereader.StudentPackage.StudentMenuActivity;
import com.google.android.gms.samples.vision.barcodereader.Classes.User;
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

public class HistoryFragment extends Fragment {
    private RecyclerView recyclerView;
    private HistoryAdapter adapter;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase, historyAsProfessor, historyAsStudent;

    TextView yourScore, infoScore, noDataLbl;
    Spinner spinner;
    Double score = 0.0;
    String keyOfSelectedStudent;
    List<AnsweredQuestion> answeredQuestions = new ArrayList<>();
    List<Question> questionsAQ = new ArrayList<>();
    List<Question> professorQuestionsAQ = new ArrayList<>();        // if not empty, then the professor wanna access history
    Map<String, List<String>> professorsSubjects = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.history_activity, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view_QS);
        yourScore = (TextView) rootView.findViewById(R.id.yourScore);
        infoScore = (TextView) rootView.findViewById(R.id.scoreTextView);
        infoScore.setVisibility(View.INVISIBLE);
        noDataLbl = (TextView) rootView.findViewById(R.id.noDataLblH);

        spinner = (Spinner) rootView.findViewById(R.id.subjectsOptions);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                onOptionSelected();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mAuth = FirebaseAuth.getInstance();

        getAnsweredQuestions();
        populateRecyclerView();

        return rootView;
    }

    public void setKeyOfSelectedStudent(String keyOfSelectedStudent) {
        this.keyOfSelectedStudent = keyOfSelectedStudent;
    }

    public void populateRecyclerView() {
        adapter = new HistoryAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity().getApplicationContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Question selectedQuestion = adapter.getMyQuestions().get(position);
                        String key = selectedQuestion.getKey();

                        SeeAQFragment seeAQFragment = new SeeAQFragment();
                        seeAQFragment.setKeys(key, keyOfSelectedStudent);

                        if (getActivity() instanceof ProfessorMenuActivity) {
                            ((ProfessorMenuActivity) getActivity()).addFragment(seeAQFragment);
                        }
                        if (getActivity() instanceof StudentMenuActivity) {
                            ((StudentMenuActivity) getActivity()).addFragment(seeAQFragment);
                        }

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                })
        );
    }

    public void getAnsweredQuestions() {
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        if (keyOfSelectedStudent != null) {
            // the professor
            historyAsProfessor = mDatabase.child(keyOfSelectedStudent);
            seeHistoryAs(historyAsProfessor);
        } else {
            // the student
            historyAsStudent = mDatabase.child(mAuth.getCurrentUser().getUid());
            seeHistoryAs(historyAsStudent);
        }
    }

    public void seeHistoryAs(DatabaseReference dbR) {
        dbR.addListenerForSingleValueEvent(new ValueEventListener() {
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
                }
                getInfoAboutAQ();
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

                            String idProfessor = q.getIdProfessor();
                            q.setIdProfessor(idProfessor);

                            String subject = q.getSubject();
                            q.setSubject(subject);

                            if (mAuth.getCurrentUser().getUid().equals(idProfessor)) {
                                professorQuestionsAQ.add(q);
                            }

                            questionsAQ.add(q);
                        }
                    }
                }
                getProfessorsData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getProfessorsData() {
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

                    // we only get the professors
                    if (typeOfUser.equals("P")) {
                        for (Question q : questionsAQ) {
                            String subject = q.getSubject();

                            if (d.getKey().equals(q.getIdProfessor())) {
                                Map<String, Professor> professorValue = (HashMap<String, Professor>) d.getValue();
                                String key = d.getKey();
                                String lastName = "";
                                String firstName = "";

                                for (HashMap.Entry i : professorValue.entrySet()) {
                                    if (i.getKey().equals("lastName")) {
                                        lastName = (String) i.getValue();
                                    }
                                    if (i.getKey().equals("firstName")) {
                                        firstName = (String) i.getValue();
                                    }
                                }

                                boolean found = false;
                                for (Map.Entry entry : professorsSubjects.entrySet()) {
                                    if (entry.getKey().equals(key)) {
                                        List<String> entryValue = (List<String>) entry.getValue();
                                        entryValue.add(subject + " - " + lastName + " " + firstName);
                                        found = true;
                                    }
                                }

                                if (!found) {
                                    List<String> value = new ArrayList<>();
                                    value.add(subject + " - " + lastName + " " + firstName);
                                    professorsSubjects.put(key, value);
                                }
                            }
                        }
                    }
                }
                if (!professorQuestionsAQ.isEmpty()) {
                    noDataLbl.setVisibility(View.INVISIBLE);
                    adapter.updateQ(professorQuestionsAQ);
                    addItemsOnSpinnerProfessor();
                } else if (!questionsAQ.isEmpty()){
                    noDataLbl.setVisibility(View.INVISIBLE);
                    adapter.updateQ(questionsAQ);
                    addItemsOnSpinnerStudent();
                } else {
                    noDataLbl.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void calculateScore(List<Question> qs) {
        score = 0.0;        // reinitialize each time

        for (Question q : qs) {
            if (q.getIsCorrect().equals("correct")) {
                score += 0.1;
            }
        }

        String scoreStr = score.toString();
        scoreStr = scoreStr.substring(0, 3);
        infoScore.setVisibility(View.VISIBLE);
        yourScore.setText(scoreStr);
    }

    // as student
    public void addItemsOnSpinnerStudent() {
        List<String> options = new ArrayList<String>();
        options.add("All subjects");

        for (Map.Entry entry : professorsSubjects.entrySet()) {
            List<String> entryValue = (List<String>) entry.getValue();
            for (String ev : entryValue) {
                boolean contains = false;

                for (String o : options) {
                    if (ev.equals(o)) {
                        contains = true;
                    }
                }
                if (contains == false) {
                    options.add(ev);
                }
            }
        }

//        Collections.sort(options, (o1, o2) -> o1.compareTo(o2));

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_item, options);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(dataAdapter);
    }

    // as professor
    public void addItemsOnSpinnerProfessor() {
        List<String> options = new ArrayList<String>();
        options.add("All subjects");

        for (Map.Entry entry : professorsSubjects.entrySet()) {
            if (mAuth.getCurrentUser().getUid().equals(entry.getKey())) {
                List<String> entryValue = (List<String>) entry.getValue();
                for (String ev : entryValue) {
                    boolean contains = false;

                    for (String o : options) {
                        if (ev.substring(0, ev.indexOf(" - ")).equals(o)) {
                            contains = true;
                        }
                    }
                    if (contains == false) {
                        options.add(ev.substring(0, ev.indexOf(" - ")));
                    }
                }
            }
        }
//        Collections.sort(options, (o1, o2) -> o1.compareTo(o2));

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_item, options);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(dataAdapter);
    }

    public void onOptionSelected() {
        String selectedOption = spinner.getSelectedItem().toString().trim();
        List<Question> selectedQuestions = new ArrayList<>();
        String selectedSubject = "";

        if (selectedOption.equals("All subjects")) {
            if (!professorQuestionsAQ.isEmpty()) {
                adapter.updateQ(professorQuestionsAQ);          // professor wanna see answers to his questions
            } else {
                adapter.updateQ(questionsAQ);                   // student wanna see all the questions
            }
        } else {
            String idProfessor = "";
            // get professor id for option selected
            for (Map.Entry entry : professorsSubjects.entrySet()) {
                List<String> entryValue = (List<String>) entry.getValue();
                for (String ev : entryValue) {
                    if (!professorQuestionsAQ.isEmpty()) {
                        // see ass professor
                        if (ev.substring(0, ev.indexOf(" - ")).equals(selectedOption)) {
                            idProfessor = entry.getKey().toString();
                            selectedSubject = ev.substring(0, ev.indexOf(" - "));
                        }
                    } else {
                        // see as student
                        if (ev.equals(selectedOption)) {
                            idProfessor = entry.getKey().toString();
                            selectedSubject = ev.substring(0, ev.indexOf(" - "));
                        }
                    }
                }
            }

            for (Question q : questionsAQ) {
                if (q.getIdProfessor().equals(idProfessor) && q.getSubject().equals(selectedSubject)) {
                    selectedQuestions.add(q);
                }
            }
            adapter.updateQ(selectedQuestions);
            calculateScore(selectedQuestions);
        }
    }
}
