package com.example.task.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.task.R;
import com.example.task.database.PostsDatabaseHelper;
import com.example.task.models.Lists;

import java.util.ArrayList;

public class ListsAdapter extends RecyclerView.Adapter<ListsAdapter.ListsViewHolder> {

    Context context;
    ArrayList<Lists> representatives;
    Onclick onclick;
    View view;
    PostsDatabaseHelper databaseHelper;

    public interface Onclick {
        void onEvent(Lists model, int pos);
    }


    public ListsAdapter(Context context, ArrayList<Lists> models, Onclick onclick) {
        this.context = context;
        this.representatives = models;
        this.onclick = onclick;
        databaseHelper = PostsDatabaseHelper.getInstance(context);

    }

    @NonNull
    @Override
    public ListsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        view = inflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        ListsViewHolder rvViewHolder = new ListsViewHolder(view);
        return rvViewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull ListsViewHolder holder, int position) {

        final Lists model = representatives.get(position);

        if (model.title != null) {
            holder.listsTitle.setText(model.getTitle());
        } else holder.listsTitle.setText(" ");


        holder.removeImg.setVisibility(View.VISIBLE);

        holder.removeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.removeImg.getVisibility() == View.VISIBLE) {
                    representatives.remove(position);
                    if(databaseHelper.deleteList(model.getId())) Toast.makeText(context, "List was successfully deleted!", Toast.LENGTH_LONG).show();
                    else Toast.makeText(context, "Error while deleting list!", Toast.LENGTH_LONG).show();
                    notifyDataSetChanged();
                }
            }
        });

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onclick.onEvent(model, position);
            }
        });

        holder.cardView.setOnClickListener(new View.OnClickListener() {
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

    public class ListsViewHolder extends RecyclerView.ViewHolder {

        TextView listsTitle;
        ImageView removeImg;
        RelativeLayout relativeLayout;
        CardView cardView;

        public ListsViewHolder(@NonNull View itemView) {
            super(itemView);
            listsTitle = itemView.findViewById(R.id.listTitle);

            removeImg = itemView.findViewById(R.id.deleteListButton);
            relativeLayout = itemView.findViewById(R.id.listLayout);
            cardView = itemView.findViewById(R.id.cardListView);
        }
    }

}
