package com.rv150.notes;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rv150.notes.Models.Category;
import com.rv150.notes.Models.Note;

import java.util.List;

/**
 * Created by Rudnev on 18.11.2016.
 */

public class RecyclerAdapter extends  RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private List<Note> mItems;
    private Context mContext;

    public RecyclerAdapter(List<Note> items, Context context) {
        this.mItems = items;
        this.mContext = context;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView mName;
        LinearLayout mCategories;

        ViewHolder(View itemView) {
            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.item_name);
            mCategories = (LinearLayout) itemView.findViewById(R.id.item_categories);
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
        final Note item = mItems.get(position);
        viewHolder.mName.setText(item.getName());

        viewHolder.mCategories.removeAllViews();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 25, 0); // params.setMargins(left, top, right, bottom);

        if (!item.getCategories().isEmpty()) {
            for (Category category : item.getCategories()) {
                TextView textView = new TextView(mContext);
                textView.setText(category.getName());
                textView.setLayoutParams(params);

                int width = (int) mContext.getResources().getDimension(R.dimen.textview_border);
                GradientDrawable drawable = (GradientDrawable) ContextCompat.getDrawable(mContext, R.drawable.textview_border);
                drawable.setStroke(width, category.getColor());

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    textView.setBackground(drawable);
                }
                else {
                    textView.setBackgroundDrawable(drawable);
                }
                viewHolder.mCategories.addView(textView);
            }
        }
        else {
            TextView textView = new TextView(mContext);
            textView.setText(R.string.without_categories);
            textView.setLayoutParams(params);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                textView.setTextColor(ContextCompat.getColor(mContext, R.color.md_grey_400));
            }
            else {
                textView.setTextColor(mContext.getResources().getColor(R.color.md_grey_400));
            }
            viewHolder.mCategories.addView(textView);
        }

    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void setItems (List<Note> items) {
        mItems = items;
        notifyDataSetChanged();
    }

    public Note getItemAtPosition (int pos) {
        return mItems.get(pos);
    }

    public void addItem(Note item) {
        this.addItem(item, mItems.size());
    }

    public void addItem(Note item, int pos) {
        mItems.add(pos, item);
        notifyItemInserted(pos);
    }

    public void updateItem (Note note) {
        int pos = mItems.indexOf(note);
        if (pos != -1) {
            mItems.set(pos, note);
            notifyItemChanged(pos);
        }
    }


    public void removeItem (Note note) {
        removeItemAtPosition(mItems.indexOf(note));
    }

    public void removeItemAtPosition (int pos) {
        if (pos >= 0 && pos < mItems.size()) {
            mItems.remove(pos);
            notifyItemRemoved(pos);
        }
    }

    public void removeAllItems() {
        mItems.clear();
        notifyDataSetChanged();
    }

}
