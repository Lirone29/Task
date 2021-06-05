package com.example.task.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.task.R;
import com.example.task.database.PostsDatabaseHelper;
import com.example.task.models.Lists;
import com.example.task.models.Task;

import java.util.ArrayList;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    Context context;
    ArrayList<Task> representatives;
    Onclick onclick;
    View view;
    PostsDatabaseHelper databaseHelper;

    public interface Onclick {
        void onEvent(Task model, int pos);
    }


    public TaskAdapter(Context context, ArrayList<Task> models, Onclick onclick) {
        this.context = context;
        this.representatives = models;
        this.onclick = onclick;
        databaseHelper = PostsDatabaseHelper.getInstance(context);
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        view = inflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        TaskViewHolder rvViewHolder = new TaskViewHolder(view);
        return rvViewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {

        final Task model = representatives.get(position);


        if (model.getContent() != null) {
            holder.taskContent.setText(model.getContent());
        } else holder.taskContent.setText(" ");


        if (model.getDate() != null) {
            holder.taskDate.setText(model.getDate());
        }

        if (model.getTime() != null) {
            holder.taskTime.setText(model.getTime());
        }

        if (model.getAccomplished() != null) {
            String text = "";
            if (model.getAccomplished() == true) text = new String("Done");
            else text = new String("To Do");

            holder.taskAccomplished.setText(text);
        } else holder.taskAccomplished.setText(" ");


        holder.accomplishImg.setVisibility(View.VISIBLE);

        holder.accomplishImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.accomplishImg.getVisibility() == View.VISIBLE) {
                    if (model.getAccomplished() == true) {
                        holder.relativeTaskLayout.setBackgroundColor(context.getResources().getColor(R.color.blue));
                        model.setAccomplished(false);
                        holder.taskAccomplished.setText("To Do");
                    } else {
                        holder.relativeTaskLayout.setBackgroundColor(context.getResources().getColor(R.color.yellow));
                        model.setAccomplished(true);
                        holder.taskAccomplished.setText("Done");
                    }
                    databaseHelper.accomplishTask(model.getId(), model.getAccomplished());
                    notifyDataSetChanged();
                }
            }
        });

        holder.relativeTaskLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onclick.onEvent(model, position);
            }
        });

        holder.taskCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onclick.onEvent(model, position);
            }
        });


    }


    @Override
    public int getItemCount() {
        return representatives.size();
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {

        TextView taskContent;
        TextView taskTime;
        TextView taskDate;
        TextView taskAccomplished;

        ImageView accomplishImg;
        RelativeLayout relativeTaskLayout;
        CardView taskCardView;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskContent = itemView.findViewById(R.id.taskContent);
            taskTime = itemView.findViewById(R.id.taskTime);
            taskDate = itemView.findViewById(R.id.taskDate);
            taskAccomplished = itemView.findViewById(R.id.accomplishedTextView);

            accomplishImg = itemView.findViewById(R.id.accomplishImageButton);
            relativeTaskLayout = itemView.findViewById(R.id.taskListLayout);
            taskCardView = itemView.findViewById(R.id.cardTaskView);
        }
    }
}
