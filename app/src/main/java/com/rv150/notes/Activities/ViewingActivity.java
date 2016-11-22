package com.rv150.notes.Activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rv150.notes.Models.Category;
import com.rv150.notes.Models.Note;
import com.rv150.notes.R;

import java.util.List;

import static com.rv150.notes.Constants.RC_MODIFYING_NOTE;
import static com.rv150.notes.Constants.RESULT_MODIFIED;

/**
 * Created by Rudnev on 21.11.2016.
 */

public class ViewingActivity extends AppCompatActivity {
    private TextView mName;
    private TextView mContent;
    private Note note;

    // Флаг, означающий, была ли заметка отредактирована
    private boolean wasModified = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewing_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_viewing);
        setSupportActionBar(toolbar);

        mName = (TextView) findViewById(R.id.output_note_name);
        mContent = (TextView) findViewById(R.id.output_note_content);

        parseIntentData(getIntent());
    }

    public void editNote (View view) {
        Intent intent = new Intent(getApplicationContext(), EditingActivity.class);
        intent.putExtra(Note.class.getSimpleName(), note);
        startActivityForResult(intent, RC_MODIFYING_NOTE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_MODIFYING_NOTE && resultCode == RESULT_OK) {
            parseIntentData(data);
            wasModified = true;
            Toast toast = Toast.makeText(getApplicationContext(),
                    R.string.note_was_updated, Toast.LENGTH_SHORT);
            toast.show();
        }
    }


    private void parseIntentData (Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            note = extras.getParcelable(Note.class.getSimpleName());
            if (note != null) {
                String name = note.getName();
                String content = note.getContent();
                mName.setText(name);
                mContent.setText(content);
                updateCategoriesTextViews(note.getCategories());
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (wasModified) {
            setResult(RESULT_MODIFIED);
        }
        else {
            setResult(RESULT_OK);
        }
        finish();
    }

    private void updateCategoriesTextViews(List<Category> categories) {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.note_categories);
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
