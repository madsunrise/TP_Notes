package com.rv150.notes.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rv150.notes.Database.DAO.NoteDAO;
import com.rv150.notes.Models.Category;
import com.rv150.notes.Models.Note;
import com.rv150.notes.R;
import com.rv150.notes.ThemeChanger;

import java.util.List;

import static com.rv150.notes.Constants.RC_MODIFYING_NOTE;
import static com.rv150.notes.Constants.RESULT_MODIFIED;
import static com.rv150.notes.Constants.RESULT_REMOVED;

/**
 * Created by Rudnev on 21.11.2016.
 */

public class ViewingActivity extends AppCompatActivity {
    private TextView mName;
    private TextView mContent;
    private Note note;
    private static final String TAG = "Viewing Activity";

    // Флаг, означающий, была ли заметка отредактирована
    private boolean wasModified = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeChanger.onActivityCreateSetTheme(this);
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
            Intent intent = new Intent();   // Передаем измененную заметку
            intent.putExtra(Note.class.getSimpleName(), note);
            setResult(RESULT_MODIFIED, intent);
        }
        else {
            setResult(RESULT_CANCELED);
        }
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_viewing_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_send) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_SUBJECT, mName.getText().toString());
            intent.putExtra(Intent.EXTRA_TEXT, mContent.getText().toString());
            intent.setType("text/plain");
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_remove) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.warning)
                    .setMessage(R.string.sure_you_want_to_delete_note)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            removeNote();
                        }
                    })
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                    .show();
        }

        return super.onOptionsItemSelected(item);
    }

    private void removeNote() {
        NoteDAO noteDAO = new NoteDAO(this);
        noteDAO.removeNote(note.getId());

        Intent intent = new Intent();
        intent.putExtra(Note.class.getSimpleName(), note);
        setResult(RESULT_REMOVED, intent);
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
}
