package com.google.android.gms.samples.vision.barcodereader.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.samples.vision.barcodereader.Question;
import com.google.android.gms.samples.vision.barcodereader.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // the info I want to see for an entry
        public TextView course, question, isCorrect;

        public ViewHolder(View v) {
            super(v);

            course = (TextView) itemView.findViewById(R.id.courseQSField);
            question = (TextView) itemView.findViewById(R.id.questionQSField);
            isCorrect = (TextView) itemView.findViewById(R.id.isCorrectQS);
        }
    }

    private List<Question> myQuestios;

    public HistoryAdapter() {
        myQuestios = new ArrayList<>();
    }

    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View questionView = inflater.inflate(R.layout.history_layout, parent, false);

        HistoryAdapter.ViewHolder viewHolder = new HistoryAdapter.ViewHolder(questionView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(HistoryAdapter.ViewHolder viewHolder, int position) {
        Question question = myQuestios.get(position);

        TextView c = viewHolder.course;
        TextView q = viewHolder.question;
        TextView isC = viewHolder.isCorrect;
        c.setText(question.getCourse());
        q.setText(question.getQuestion());
        isC.setText( question.isCorrect());
    }

    @Override
    public int getItemCount() {
        return myQuestios.size();
    }

    public void updateQ(List<Question> newQList) {
        myQuestios.clear();
        myQuestios.addAll(newQList);
        Collections.sort(myQuestios, (o1, o2) -> o1.getCourse().compareTo(o2.getCourse()));
        this.notifyDataSetChanged();
    }

    public List<Question> getMyQuestions() {
        return myQuestios;
    }
}
