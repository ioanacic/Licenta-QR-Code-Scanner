package com.google.android.gms.samples.vision.barcodereader;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // the info I want to see for an entry
        public TextView course, question, info;
        ImageButton addQuestion, editQuestion;
        ImageView qrImageView;
        public Question questionQ;      // the selected Q

        SeeQuestionListener listener;

        public ViewHolder(View v) {
            super(v);

            course = (TextView) itemView.findViewById(R.id.courseField);
            question = (TextView) itemView.findViewById(R.id.questionField);
            info.setVisibility(View.INVISIBLE);
        }

        public ViewHolder(View v, final SeeQuestionListener listener) {
            super(v);
            this.listener = listener;

            course = (TextView) itemView.findViewById(R.id.courseField);
            question = (TextView) itemView.findViewById(R.id.questionField);

            addQuestion = (ImageButton) itemView.findViewById(R.id.addQuestionToTest);
            editQuestion = (ImageButton) itemView.findViewById(R.id.editQuestionButton);

            qrImageView = (ImageView) itemView.findViewById(R.id.qrImageView);

            addQuestion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onAddQButtonClicked(questionQ);
                }
            });

            editQuestion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onEditButtonClicked(questionQ);
                }
            });

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onViewClicked(questionQ);
                }
            });

            v.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    listener.onViewLongClicked(view, questionQ);
                    return true;
                }
            });
        }
    }

    private List<Question> myQuestios;

    SeeQuestionListener listener;

    public QuestionsAdapter(SeeQuestionListener listener) {
        myQuestios = new ArrayList<>();
        this.listener = listener;
    }

    @Override
    public QuestionsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View questionView = inflater.inflate(R.layout.see_question_layout, parent, false);

        ViewHolder viewHolder = new ViewHolder(questionView, listener);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Question question = myQuestios.get(position);

        TextView c = viewHolder.course;
        TextView q = viewHolder.question;
        c.setText(question.getCourse());
        q.setText(question.getQuestion());

        viewHolder.questionQ = question;

        if (!question.isSelected()) {
            // = false = nu a mai fost apasat
            viewHolder.addQuestion.setImageResource(R.drawable.ic_check_circle_outline_24px);
        } else {
            // = true = a mai fost apasat = resetez

            viewHolder.addQuestion.setImageResource(R.drawable.ic_check_circle_24px);
        }

        if (question.getIsQrGenerated()) {
            viewHolder.qrImageView.setVisibility(View.VISIBLE);
        } else {
            viewHolder.qrImageView.setVisibility(View.INVISIBLE);
        }
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