package com.rv150.notes.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.rv150.notes.Database.DAO.CategoryDAO;
import com.rv150.notes.Database.DAO.NoteDAO;
import com.rv150.notes.Models.Category;
import com.rv150.notes.Models.Note;
import com.rv150.notes.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rudnev on 16.11.2016.
 */

public class AddingActivity extends AppCompatActivity {
    private EditText mName;
    private EditText mContent;
    private NoteDAO mNoteDAO;
    private CategoryDAO mCategoryDAO;

    private List<Category> mChoosenCategories = new ArrayList<>();
    private List<Category> mAllCategories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_adding);
        setSupportActionBar(toolbar);

        mName = (EditText) findViewById(R.id.input_note_name);
        mContent = (EditText) findViewById(R.id.input_note_content);

        mNoteDAO = new NoteDAO(getApplicationContext());
        mCategoryDAO = new CategoryDAO(getApplicationContext());
        mAllCategories = mCategoryDAO.getAll();
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

       Note note = new Note(name, content);
       note.setCategories(mChoosenCategories);
       mNoteDAO.insertNote(note);

       setResult(RESULT_OK);
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

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.choose_categories)
                .setMultiChoiceItems(existingCatNames,selectedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                    }
                })
                // Set the action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mChoosenCategories.clear();
                        for (int i = 0; i < size; ++i) {
                            if (selectedItems[i]) {
                                Category category = mAllCategories.get(i);
                                mChoosenCategories.add(category);
                            }
                        }
                        updateCategoriesTextViews(mChoosenCategories);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .show();
    }


    private void updateCategoriesTextViews(List<Category> categories) {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.categories_lin_layout);
        linearLayout.removeAllViews();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 25, 0); // params.setMargins(left, top, right, bottom);
        for (Category category: categories) {
            TextView textView = new TextView(this);
            textView.setText(category.getName());
            textView.setLayoutParams(params);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                textView.setBackground(ContextCompat.getDrawable(this, R.drawable.textview_border));
            }
            else {
                textView.setBackgroundDrawable(getResources().getDrawable(R.drawable.textview_border));
            }
            linearLayout.addView(textView);
        }
    }
}
