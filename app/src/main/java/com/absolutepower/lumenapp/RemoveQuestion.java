package com.absolutepower.lumenapp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.EditText;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class RemoveQuestion extends AsyncTask<String, String, String> {
    public Context context;

    public RemoveQuestion(Context context) {
        this.context = context;
    }

    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... arg0) {
        try {
            String questionIndex = arg0[0];

            String link = "http://10.0.2.2:80/android_connect/www/removequestion.php";
            String data = URLEncoder.encode("questionIndex", "UTF-8") + "=" +
                    URLEncoder.encode(questionIndex, "UTF-8");

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
            //TODO: why tf isn't it catching the error???
            try {
                return sb.toString().trim();
            } catch (NullPointerException e){
                return "0";
            }

        } catch (Exception e) {
            return new String("Exception: " + e.getMessage());
        }
    }

    @Override
    protected void onPostExecute(String result) {
        Intent intent = new Intent(this.context, VideoActivity.class);
        this.context.startActivity(intent);
    }
}
