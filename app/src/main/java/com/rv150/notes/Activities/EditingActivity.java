package com.rv150.notes.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.rv150.notes.Database.DAO.CategoryDAO;
import com.rv150.notes.Database.DAO.NoteDAO;
import com.rv150.notes.Models.Category;
import com.rv150.notes.Models.Note;
import com.rv150.notes.R;
import com.rv150.notes.ThemeChanger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rudnev on 16.11.2016.
 */

public class EditingActivity extends AppCompatActivity {
    private EditText mName;
    private EditText mContent;
    private NoteDAO mNoteDAO;

    private List<Category> mChoosenCategories = new ArrayList<>();
    private List<Category> mAllCategories;

    // Флаг, означающий добавляем мы заметку или изменяем существующую
    private boolean isModifying = false;
    private Note mNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeChanger.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_editing_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_adding);
        setSupportActionBar(toolbar);

        mName = (EditText) findViewById(R.id.input_note_name);
        mContent = (EditText) findViewById(R.id.input_note_content);

        mNoteDAO = new NoteDAO(getApplicationContext());
        CategoryDAO categoryDAO = new CategoryDAO(getApplicationContext());
        mAllCategories = categoryDAO.getAll();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            isModifying = true;
            setTitle(R.string.modifying_note);
            mNote = extras.getParcelable(Note.class.getSimpleName());
            if (mNote != null) {
                mChoosenCategories = mNote.getCategories();
                updateCategoriesTextViews(mChoosenCategories);
                mName.setText(mNote.getName());
                mContent.setText(mNote.getContent());
            }
        } else {
            mNote = new Note();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("choosenCategories", (ArrayList<? extends Parcelable>) mChoosenCategories);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mChoosenCategories = savedInstanceState.getParcelableArrayList("choosenCategories");
        if (mChoosenCategories != null) {
            updateCategoriesTextViews(mChoosenCategories);
        }
    }

    public void chooseCategories(View view) {
        if (mAllCategories.isEmpty()) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    R.string.no_created_categories, Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        ListView listView = new ListView(this);

        final CustomAdapter adapter = new CustomAdapter(this, R.layout.categories_dialog_item, mAllCategories);
        adapter.setCheckedItems(mChoosenCategories);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final CheckedTextView checkedTextView = (CheckedTextView) view.findViewById(R.id.checkedTV);
                boolean wasChecked = checkedTextView.isChecked();
                checkedTextView.setChecked(!wasChecked);
                adapter.setCheckedStatus(i, !wasChecked);
            }
        });

        new AlertDialog.Builder(this)
                .setTitle(R.string.choose_categories)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mChoosenCategories = adapter.getCheckedItems();
                        updateCategoriesTextViews(mChoosenCategories);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .setView(listView)
                .show();
    }



    private void updateCategoriesTextViews(List<Category> categories) {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.categories_lin_layout);
        linearLayout.removeAllViews();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 25, 0);  // params.setMargins(left, top, right, bottom);
        for (Category category: categories) {
            TextView textView = new TextView(this);
            textView.setText(category.getName());
            textView.setLayoutParams(params);


            // Установим цвет обводки textView
            int width = (int) getResources().getDimension(R.dimen.textview_border);
            GradientDrawable drawable = (GradientDrawable) ContextCompat.getDrawable(this, R.drawable.textview_border);
            drawable.setStroke(width, category.getColor());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ThemeChanger.getTheme() == ThemeChanger.THEME_LIGHT) {
                    drawable.setColor(getColor(R.color.md_light_background));
                    textView.setTextColor(getColor(R.color.md_black_1000));
                }
                if (ThemeChanger.getTheme() == ThemeChanger.THEME_DARK) {
                    drawable.setColor(getColor(R.color.md_dark_background));
                    textView.setTextColor(getColor(R.color.md_white_1000));
                }
                textView.setBackground(drawable);
            }
            else {
                if (ThemeChanger.getTheme() == ThemeChanger.THEME_LIGHT) {
                    drawable.setColor(getResources().getColor(R.color.md_light_background));
                    textView.setTextColor(getResources().getColor(R.color.md_black_1000));
                }
                if (ThemeChanger.getTheme() == ThemeChanger.THEME_DARK) {
                    drawable.setColor(getResources().getColor(R.color.md_dark_background));
                    textView.setTextColor(getResources().getColor(R.color.md_white_1000));
                }
                textView.setBackgroundDrawable(drawable);
            }

            linearLayout.addView(textView);
        }
    }

    public void saveNote(View view) {
        String name = mName.getText().toString();
        String content = mContent.getText().toString();
        if (name.isEmpty() || content.isEmpty()) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    R.string.fill_all_fields, Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        mNote.setName(name);
        mNote.setContent(content);
        mNote.setCategories(mChoosenCategories);

        Intent intent = new Intent();
        intent.putExtra(Note.class.getSimpleName(), mNote);
        setResult(RESULT_OK, intent);

        // Сохраняем в базе
        if (isModifying) {
            mNoteDAO.updateNote(mNote);
        } else {
            mNoteDAO.insertNote(mNote);
        }

        finish();
    }

    private class CustomAdapter extends ArrayAdapter<Category>{

        private boolean[] checkedPositions;
        private List<Category> mItems;

        CustomAdapter (Context context, int resource, List<Category> items) {
            super(context, resource, items);
            this.mItems = items;
            checkedPositions = new boolean[mItems.size()];
        }

        void setCheckedItems (List<Category> checkedItems) {
            if (checkedItems == null || checkedItems.isEmpty()) {
                return;
            }
            for (int i = 0; i < mItems.size(); ++i) {
                boolean check = checkedItems.contains(mItems.get(i));
                checkedPositions[i] = check;
            }
        }

        List<Category> getCheckedItems(){
            List<Category> checkedItems = new ArrayList<>();
            for (int i = 0; i < mItems.size(); ++i) {
                if (checkedPositions[i]) {
                    checkedItems.add(mItems.get(i));
                }
            }
            return checkedItems;
        }

        void setCheckedStatus (int position, boolean checked) {
            if (position >= 0 && position < checkedPositions.length) {
                checkedPositions[position] = checked;
            }

        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = getLayoutInflater().inflate(R.layout.categories_dialog_item, parent, false);
            }
            Category item = mItems.get(position);

            final CheckedTextView checkedTextView = (CheckedTextView) convertView.findViewById(R.id.checkedTV);
            checkedTextView.setText(item.getName());
            boolean isChecked = checkedPositions[position];
            checkedTextView.setChecked(isChecked);


            ImageView imageView = (ImageView) convertView.findViewById(R.id.icon);
            imageView.setColorFilter(item.getColor(), PorterDuff.Mode.MULTIPLY);

            return convertView;
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage(isModifying? R.string.undo_editing : R.string.undo_adding_note)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditingActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }
}
