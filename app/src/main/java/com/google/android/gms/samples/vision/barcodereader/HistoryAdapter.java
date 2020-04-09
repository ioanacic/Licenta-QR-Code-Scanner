package com.google.android.gms.samples.vision.barcodereader;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

    public HistoryAdapter(List<Question> questions) {
        myQuestios = questions;
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
}
