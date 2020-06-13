package com.google.android.gms.samples.vision.barcodereader.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.samples.vision.barcodereader.Classes.Test;
import com.google.android.gms.samples.vision.barcodereader.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestsAdapter extends RecyclerView.Adapter<TestsAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // the info I want to see for an entry
        public TextView title, numberOfQuestions;

        public ViewHolder(View v) {
            super(v);

            title = (TextView) itemView.findViewById(R.id.titleField);
            numberOfQuestions = (TextView) itemView.findViewById(R.id.numberOfQuestionsField);
        }
    }

    private List<Test> myTests;

    public TestsAdapter() {
        myTests = new ArrayList<>();
    }

    @Override
    public TestsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View testView = inflater.inflate(R.layout.see_test_layout, parent, false);

        TestsAdapter.ViewHolder viewHolder = new TestsAdapter.ViewHolder(testView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TestsAdapter.ViewHolder viewHolder, int position) {
        Test test = myTests.get(position);

        TextView t = viewHolder.title;
        TextView nr = viewHolder.numberOfQuestions;
        t.setText(test.getTitle());
        nr.setText("Number of questions: " + test.getNumberOfQuestions());
    }

    @Override
    public int getItemCount() {
        return myTests.size();
    }

    public void updateT(List<Test> newTList) {
        myTests.clear();
        myTests.addAll(newTList);
        Collections.sort(myTests, (o1, o2) -> o1.getTitle().compareTo(o2.getTitle()));
        this.notifyDataSetChanged();
    }


    public List<Test> getMyTests() {
        return myTests;
    }
}
