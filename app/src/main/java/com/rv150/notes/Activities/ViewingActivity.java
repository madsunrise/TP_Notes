package com.rv150.notes.Activities;

import android.os.Bundle;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.rv150.notes.Constants;
import com.rv150.notes.Database.DBHelper;
import com.rv150.notes.Models.Note;
import com.rv150.notes.R;

/**
 * Created by Rudnev on 21.11.2016.
 */

public class ViewingActivity extends AppCompatActivity {
    private TextView mName;
    private TextView mContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewing_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_viewing);
        setSupportActionBar(toolbar);

        mName = (TextView) findViewById(R.id.output_note_name);
        mContent = (TextView) findViewById(R.id.output_note_content);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Note note = extras.getParcelable(Note.class.getSimpleName());
            if (note != null) {
                String name = note.getName();
                String content = note.getContent();
                mName.setText(name);
                mContent.setText(content);
            }
        }
    }
}
