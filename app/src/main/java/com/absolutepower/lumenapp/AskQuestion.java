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
import android.widget.Toast;

public class AskQuestion extends AsyncTask<String, String, String>{
    public Context context;

    public AskQuestion(Context context){
        this.context = context;
    }

    protected void onPreExecute(){
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... arg0) {
        try{
            String asker = arg0[0];
            String question = arg0[1];
            String course = arg0[2];
            String type = arg0[3];

            String link="http://10.0.2.2:80/android_connect/www/addquestion.php";
            String data = URLEncoder.encode("asker", "UTF-8") + "=" +
                    URLEncoder.encode(asker, "UTF-8");
            data += "&" + URLEncoder.encode("question", "UTF-8") + "=" +
                    URLEncoder.encode(question, "UTF-8");
            data += "&" + URLEncoder.encode("course", "UTF-8") + "=" +
                    URLEncoder.encode(course, "UTF-8");
            data += "&" + URLEncoder.encode("type", "UTF-8") + "=" +
                    URLEncoder.encode(type, "UTF-8");

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
        }

        catch(Exception e){
            return new String("Exception: " +  e.getMessage()); }
    }


    @Override
    protected void onPostExecute(String result){
        HomeActivity.savedState = "learn";
        Intent intent = new Intent(this.context, VideoActivity.class);
        this.context.startActivity(intent);
    }
}