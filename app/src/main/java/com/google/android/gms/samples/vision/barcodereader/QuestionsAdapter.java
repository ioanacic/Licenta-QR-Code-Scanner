package com.google.android.gms.samples.vision.barcodereader;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // the info I want to see for an entry
        public TextView course, question, info;

        public ViewHolder(View v) {
            super(v);

            course = (TextView) itemView.findViewById(R.id.courseField);
            question = (TextView) itemView.findViewById(R.id.questionField);
            info = (TextView) itemView.findViewById(R.id.infoMsgField);
            info.setVisibility(View.INVISIBLE);
        }
    }

    private List<Question> myQuestios;

    public QuestionsAdapter() {
        myQuestios = new ArrayList<>();
    }

    @Override
    public QuestionsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View questionView = inflater.inflate(R.layout.see_question_layout, parent, false);

        ViewHolder viewHolder = new ViewHolder(questionView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Question question = myQuestios.get(position);

        TextView c = viewHolder.course;
        TextView q = viewHolder.question;
        c.setText(question.getCourse());
        q.setText(question.getQuestion());
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