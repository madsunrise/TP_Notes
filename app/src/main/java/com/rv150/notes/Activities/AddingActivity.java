package com.rv150.notes.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.rv150.notes.Database.DAO.CategoryDAO;
import com.rv150.notes.Database.DAO.NoteDAO;
import com.rv150.notes.Models.Note;
import com.rv150.notes.R;

/**
 * Created by Rudnev on 16.11.2016.
 */

public class AddingActivity extends AppCompatActivity {
    private EditText mName;
    private EditText mContent;
    private NoteDAO mNoteDAO;
    private CategoryDAO mCategoryDAO;

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

        /* Подхват категорий */

       Note note = new Note(name, content);
       mNoteDAO.insertNote(note);
       finish();
    }
}
