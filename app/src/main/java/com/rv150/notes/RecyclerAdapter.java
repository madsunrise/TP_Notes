package com.rv150.notes;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rv150.notes.Models.Note;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Rudnev on 18.11.2016.
 */

public class RecyclerAdapter extends  RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private List<Note> items;

    public RecyclerAdapter(List<Note> items) {
        this.items = items;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView mName;
        TextView mCategories;

        ViewHolder(View itemView) {
            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.item_name);
            mCategories = (TextView) itemView.findViewById(R.id.item_categories);
        }
    }

    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.main_list_item, parent, false);
        return new ViewHolder(contactView);
    }


    @Override
    public void onBindViewHolder(RecyclerAdapter.ViewHolder viewHolder, int position) {
        final Note item = items.get(position);
        //viewHolder.itemView.setBackgroundColor(Color.WHITE);
        viewHolder.mName.setText(item.getName());
        viewHolder.mCategories.setText("Категории");
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
