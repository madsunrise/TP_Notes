package com.rv150.notes.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
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
import com.rv150.notes.Utils;

import java.util.ArrayList;
import java.util.HashMap;
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
    private Note note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.onActivityCreateSetTheme(this);
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
            note = extras.getParcelable(Note.class.getSimpleName());
            if (note != null) {
                mChoosenCategories = note.getCategories();
                updateCategoriesTextViews(mChoosenCategories);
                mName.setText(note.getName());
                mContent.setText(note.getContent());
            }
        }
        else {
            note = new Note();
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

        note.setName(name);
        note.setContent(content);
        note.setCategories(mChoosenCategories);

        Intent intent = new Intent();
        intent.putExtra(Note.class.getSimpleName(), note);
        setResult(RESULT_OK, intent);

        // Сохраняем в базе
        if (isModifying) {
            mNoteDAO.updateNote(note);
        }
        else {
            mNoteDAO.insertNote(note);
        }

        finish();
    }





    public void chooseCategories(View view) {

        if (mAllCategories.isEmpty()) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    R.string.no_created_categories, Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        final int size = mAllCategories.size();
        // Получаем список имен для категорий
        String [] existingCatNames = new String[size];
        for (int i = 0; i < size; ++i) {
            existingCatNames[i] = mAllCategories.get(i).getName();
        }

        // Отмечаем категории, которые могли быть отмечены ранее
        final boolean [] selectedItems = new boolean[size];
        for (int i = 0; i < size; ++i) {
            Category category = mAllCategories.get(i);
            if (mChoosenCategories.contains(category)) {
                selectedItems[i] = true;
            }
        }

//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle(R.string.choose_categories)
//                .setMultiChoiceItems(existingCatNames,selectedItems, new DialogInterface.OnMultiChoiceClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
//                    }
//                })
//                // Set the action buttons
//                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int id) {
//                        mChoosenCategories.clear();
//                        for (int i = 0; i < size; ++i) {
//                            if (selectedItems[i]) {
//                                Category category = mAllCategories.get(i);
//                                mChoosenCategories.add(category);
//                            }
//                        }
//                        updateCategoriesTextViews(mChoosenCategories);
//                    }
//                })
//                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.cancel();
//                    }
//                })
//                .show();


        ListView listView = new ListView(this);


        final CustomAdapter adapter = new CustomAdapter(this, R.layout.categories_dialog_item, R.id.item_name, mAllCategories);
        adapter.setCheckedItems(mChoosenCategories);

        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setDivider(null);


        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(R.string.choose_categories)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mChoosenCategories = adapter.getCheckedItems();
                        updateCategoriesTextViews(mChoosenCategories);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setView(listView)
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        Toast toast = Toast.makeText(getApplicationContext(),
                                R.string.adding_note, Toast.LENGTH_SHORT);
                        toast.show();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                        Toast toast = Toast.makeText(getApplicationContext(),
                                R.string.fill_all_fields, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });

        View view1 = getLayoutInflater().inflate(R.layout.categories_dialog_item, null);
        CheckBox checkBox = (CheckBox) view1.findViewById(R.id.checkbox);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                                                @Override
                                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                    Toast toast = Toast.makeText(getApplicationContext(),
                                                            R.string.fill_all_fields, Toast.LENGTH_SHORT);
                                                    toast.show();
                                                }
                                            }
        );


        builder.show();
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                textView.setBackground(ContextCompat.getDrawable(this, R.drawable.textview_border));
            }
            else {
                textView.setBackgroundDrawable(getResources().getDrawable(R.drawable.textview_border));
            }
            linearLayout.addView(textView);
        }

    }

    private class CustomAdapter extends ArrayAdapter<Category>{

        private boolean[] checkedPositions;
        private List<Category> mItems;

        CustomAdapter(Context context, int resource,
                              int textViewResourceId, List<Category> items) {
            super(context, resource, textViewResourceId, items);
            mItems = items;
            checkedPositions = new boolean[items.size()];
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

        void setCheckedItems (List<Category> checkedItems) {
            for (int i = 0; i < mItems.size(); ++i) {
                checkedPositions[i] = checkedItems.contains(mItems.get(i));
            }
        }



        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = getLayoutInflater().inflate(R.layout.categories_dialog_item, parent, false);
            }
            Category item = mItems.get(position);

            CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkbox);
            checkBox.setText(item.getName());

            ImageView imageView = (ImageView) convertView.findViewById(R.id.icon);
            imageView.setColorFilter(item.getColor(), PorterDuff.Mode.MULTIPLY);


            checkedPositions[position] = checkBox.isChecked();
            return convertView;
        }


    }
}
