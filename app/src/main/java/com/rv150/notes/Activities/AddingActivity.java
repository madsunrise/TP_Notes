package com.rv150.notes.Activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.rv150.notes.R;

/**
 * Created by Rudnev on 16.11.2016.
 */

public class AddingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_adding);
        setSupportActionBar(toolbar);

    }
}
