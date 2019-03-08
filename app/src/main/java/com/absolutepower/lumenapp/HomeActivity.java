package com.absolutepower.lumenapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.*;
import java.util.HashMap;

public class HomeActivity extends AppCompatActivity {

    private EditText mQuestion;
    private Spinner mCourse;
    private Spinner mType;

    private Context context;
    private Boolean shouldRemoveQuestion = false;

    public static String[] courseCodes = {"MCV",
            "ENG",
            "SCH",
            "SBI",
            "SPH",
            "CGE",
            "HIS",
            "BUS",
            "FSF",
            "SPN",};
    public static String[] courses = { "Mathematics",
            "English",
            "Chemistry",
            "Biology",
            "Physics",
            "Geography",
            "History",
            "Business",
            "French",
            "Spanish"};

    public static String savedState = "home";

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            context = getApplicationContext();
            switch (item.getItemId()) {

                case R.id.navigation_home:

                    setContentView(R.layout.activity_home);
                    //set personalized welcome message
                    TextView mTextView = findViewById(R.id.textView2);
                    mTextView.setText("Welcome back " + Authentication.userData[2] + "!");

                    String[] favouriteTutors = Authentication.userData[6].split("\\*");

                    //the array adapter to load favourite tutors data into list
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, favouriteTutors);

                    //attaching adapter to favourite tutors listview
                    ListView listView = findViewById(R.id.listView1);
                    listView.setAdapter(arrayAdapter);

                    //Parse course string into course list
                    ArrayList<String> userCourseCodes = new ArrayList<>();
                    for(int x = 0; x < Authentication.userData[5].length()-2; x+=3){
                        userCourseCodes.add(Authentication.userData[5].substring(x, x+3));
                    }

                    String[] userCourses = new String[userCourseCodes.size()];

                    //Converts course codes into course names
                    for(int x = 0; x < userCourseCodes.size(); x++){
                        userCourses[x] = courses[java.util.Arrays.asList(courseCodes).indexOf(userCourseCodes.get(x))];
                    }

                    //the array adapter to load courses data into list
                    ArrayAdapter<String> arrayAdapterCourses = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, userCourses);

                    //attaching adapter to courses listview
                    ListView listView2 = findViewById(R.id.listView2);
                    listView2.setAdapter(arrayAdapterCourses);

                    BottomNavigationView navigation = findViewById(R.id.navigation);
                    navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

                    Button mLogOutButton = findViewById(R.id.button2);
                    mLogOutButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(context, HomeActivity.class);
                            context.startActivity(intent);
                        }
                    });

                    return true;

                case R.id.navigation_learn:
                    //Learn page
                    setContentView(R.layout.activity_home_dashboard);

                    mQuestion = findViewById(R.id.editText);
                    mCourse = findViewById(R.id.spinner);
                    mType = findViewById(R.id.spinner3);

                    Button mAskQuestionButton = findViewById(R.id.button3);
                    mAskQuestionButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            askQuestion();
                        }
                    });

                    BottomNavigationView navigation2 = findViewById(R.id.navigation);
                    navigation2.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

                    return true;

                case R.id.navigation_teach:
                    //Teach page
                    setContentView(R.layout.activity_home_settings);

                    if (RetrieveQuestions.questions.size() == 0) {
                        new RetrieveQuestions(context).execute(Authentication.userData[3]);
                    }

                    ArrayList<JSONObject> questions = RetrieveQuestions.questions;

                    final ListView listView3 = findViewById(R.id.listView3);
                    listView3.setAdapter(new CustomBaseAdapter(context, questions));

                    BottomNavigationView navigation3 = findViewById(R.id.navigation);
                    navigation3.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
                    listView3.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            JSONObject o = (JSONObject) listView3.getItemAtPosition(position);
                            try {
                                if (o.getString("type").equals("Individual tutoring")) {
                                    shouldRemoveQuestion = true;
                                }
                                new GetToken(context, shouldRemoveQuestion).execute(Authentication.userData[0], o.getString("questionIndex"));
                            } catch (JSONException e) {}
                        }
                    });

                    Button mButton = findViewById(R.id.button);
                    mButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Spinner spinner = findViewById(R.id.spinner2);
                            String course = courseCodes[java.util.Arrays.asList(courses).indexOf(spinner.getSelectedItem().toString())];
                            new RetrieveQuestions(context).execute(Authentication.userData[3], course);
                        }
                    });

                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if (savedState.equals("home")) {
            navigation.setSelectedItemId(R.id.navigation_home);
        } else if (savedState.equals("learn")) {
            navigation.setSelectedItemId(R.id.navigation_learn);
        } else { //if savedState is "teach
            navigation.setSelectedItemId(R.id.navigation_teach);
        }

    }

    private void askQuestion() {
        context = getApplicationContext();
        String question = mQuestion.getText().toString();
        String course = mCourse.getSelectedItem().toString();
        String type = mType.getSelectedItem().toString();
        new AskQuestion(context).execute(Authentication.userData[0], question, course, type);
    }

}
