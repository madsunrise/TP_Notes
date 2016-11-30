package com.rv150.notes.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.rv150.notes.R;

/**
 * Created by Rudnev on 29.11.2016.
 */

// Активити с информацией о разработчике
public class AboutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_about);
    }

    public void closeActivity (View view) {
        finish();
    }
}