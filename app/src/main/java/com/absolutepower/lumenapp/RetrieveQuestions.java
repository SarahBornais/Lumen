package com.absolutepower.lumenapp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class RetrieveQuestions extends AsyncTask<String, String, String>{

    public Context context;
    public static ArrayList<JSONObject> questions = new ArrayList<>();

    public RetrieveQuestions(Context context){
        this.context = context;
    }

    protected void onPreExecute(){
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... arg0) {
        try{
            String rating = arg0[0];
            String course = arg0[1];

            String link="http://10.0.2.2:80/android_connect/www/getquestion.php";
            String data = URLEncoder.encode("rating", "UTF-8") + "=" +
                    URLEncoder.encode(rating, "UTF-8");
            data += URLEncoder.encode("course", "UTF-8") + "=" +
                    URLEncoder.encode(course, "UTF-8");

            URL url = new URL(link);
            URLConnection conn = url.openConnection();

            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

            wr.write(data);
            wr.flush();

            BufferedReader reader = new BufferedReader(new
                    InputStreamReader(conn.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String json;

            // Read Server Response
            while((json = reader.readLine()) != null) {
                sb.append(json);
                break; }

            return sb.toString().trim();

        } catch(Exception e) {
            return new String("Exception: " +  e.getMessage()); }
    }

    @Override
    protected void onPostExecute(String result){
        try {
            loadIntoListView(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HomeActivity.savedState = "teach";
        Intent intent = new Intent(this.context, HomeActivity.class);
        this.context.startActivity(intent);
    }

    private void loadIntoListView(String json) throws JSONException {
        //creating a json array from the json string
        JSONArray jsonArray = new JSONArray(json);

        System.out.println("%%" + jsonArray.length());
        //looping through all the elements in json array
        for (int i = 0; i < jsonArray.length(); i++) {

            //getting json object from the json array
            JSONObject obj = jsonArray.getJSONObject(i);

            //getting the name from the json object and putting it inside string array
            questions.add(obj);
        }
    }

}
