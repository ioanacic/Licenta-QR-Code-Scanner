package com.google.android.gms.samples.vision.barcodereader;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class StudentsAdapter extends RecyclerView.Adapter<StudentsAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // the info I want to see for an entry
        public TextView name, group;

        public ViewHolder(View v) {
            super(v);

            name = (TextView) itemView.findViewById(R.id.nameField);
            group = (TextView) itemView.findViewById(R.id.groupField);
        }
    }

    private List<Student> myStudents;

    public StudentsAdapter(List<Student> students) {
        myStudents = students;
    }

    @Override
    public StudentsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View studentView = inflater.inflate(R.layout.see_student_layout, parent, false);

        StudentsAdapter.ViewHolder viewHolder = new StudentsAdapter.ViewHolder(studentView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(StudentsAdapter.ViewHolder viewHolder, int position) {
        Student student = myStudents.get(position);

        TextView n = viewHolder.name;
        TextView g = viewHolder.group;
        n.setText(student.getLastName() + " " + student.getFirstName());
        g.setText(student.getGroup());
    }

    @Override
    public int getItemCount() {
        return myStudents.size();
    }
}
