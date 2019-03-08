package com.absolutepower.lumenapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

public class CreateAccountActivity extends AppCompatActivity {

    private String firstName;
    private String email;
    private String password;
    private String rating = "0";
    private String courses;
    private String gradeLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        EditText firstNameText = findViewById(R.id.editText4);
        EditText emailText = findViewById(R.id.editText2);
        EditText passwordText = findViewById(R.id.editText5);
        EditText passwordConfirmText = findViewById(R.id.editText3);

        firstName = firstNameText.getText().toString();
        email = emailText.getText().toString();
        if (passwordText.getText().toString().equals(passwordConfirmText.getText().toString())) {
            password = passwordText.getText().toString();
        }

        Spinner gradeSpinner = findViewById(R.id.spinner5);
        gradeLevel = gradeSpinner.getSelectedItem().toString();

        CheckBox englishBox = findViewById(R.id.checkBox7);
        CheckBox frenchBox = findViewById(R.id.checkBox6);
        CheckBox spanishBox = findViewById(R.id.checkBox8);
        CheckBox mathematicsBox = findViewById(R.id.checkBox5);
        CheckBox chemistryBox = findViewById(R.id.checkBox10);
        CheckBox physicsBox = findViewById(R.id.checkBox11);
        CheckBox biologyBox = findViewById(R.id.checkBox12);
        CheckBox historyBox = findViewById(R.id.checkBox9);
        CheckBox geographyBox = findViewById(R.id.checkBox14);
        CheckBox businessBox = findViewById(R.id.checkBox13);

        if (englishBox.isChecked()) {
            courses += "ENG";
        }
        if (frenchBox.isChecked()) {
            courses += "FSF";
        }
        if (spanishBox.isChecked()) {
            courses += "SPN";
        }
        if (mathematicsBox.isChecked()) {
            courses += "MAT";
        }
        if (chemistryBox.isChecked()) {
            courses += "SCH";
        }
        if (physicsBox.isChecked()) {
            courses += "SPH";
        }
        if (biologyBox.isChecked()) {
            courses += "SBI";
        }
        if (historyBox.isChecked()) {
            courses += "HIS";
        }
        if (geographyBox.isChecked()) {
            courses += "CGE";
        }
        if (businessBox.isChecked()) {
            courses += "BUS";
        }

        Button mNewAccountButton = findViewById(R.id.button4);
        mNewAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount();
            }
        });

    }

    public void createAccount(){
        new CreateAccount(this).execute(email,password,firstName,rating,courses,gradeLevel);
    }
}
