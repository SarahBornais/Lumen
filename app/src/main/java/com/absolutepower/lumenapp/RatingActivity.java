package com.absolutepower.lumenapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;

public class RatingActivity extends AppCompatActivity {

    public static Boolean addToFavourites = false;
    public static String subject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        EditText editText = findViewById(R.id.editText6);
        RatingBar ratingBar = findViewById(R.id.ratingBar);
        CheckBox checkBox = findViewById(R.id.checkBox2);
        Spinner spinner = findViewById(R.id.spinner4);

        if (!editText.getText().toString().equals("")) {
            float rating = ratingBar.getRating();
            if (checkBox.isChecked() == true) {
                addToFavourites = true;
                subject = spinner.getSelectedItem().toString();
            }
            new UpdateRating(this).execute(editText.getText().toString(), String.valueOf(rating));
        }
    }
}
