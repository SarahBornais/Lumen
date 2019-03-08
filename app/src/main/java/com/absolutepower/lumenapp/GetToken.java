package com.absolutepower.lumenapp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.EditText;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class GetToken extends AsyncTask<String, String, String>{
    public Context context;
    public static String accessToken;

    private String identity;
    private String room;
    public static Boolean shouldRemoveQuestion;

    public GetToken(Context context, Boolean isTutor){
        this.context = context;
        shouldRemoveQuestion = isTutor;

    }

    protected void onPreExecute(){
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... arg0) {
        try{
            identity = arg0[0];
            room = arg0[1];

            String link="https://olive-rail-4493.twil.io/video-token?identity=sarah";
            String data = URLEncoder.encode("identity", "UTF-8") + "=" +
                    URLEncoder.encode(identity, "UTF-8");
            data += "&" + URLEncoder.encode("room", "UTF-8") + "=" +
                    URLEncoder.encode(room, "UTF-8");

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
        if (result.equals("0")){
            goBack();
        } else {
            HomeActivity.savedState = "teach";

            if (shouldRemoveQuestion == true) {
                new RemoveQuestion(context).execute(room);
            }

            try {
                loadIntoArray(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Intent intent = new Intent(this.context, VideoActivity.class);
            intent.putExtra("ROOM_NUMBER", room);
            this.context.startActivity(intent);
        }
    }

    private void loadIntoArray(String json) throws JSONException {
        JSONObject object = new JSONObject(json);
        accessToken = object.getString("token");
    }

    private void goBack() {
        Intent intent = new Intent(this.context, HomeActivity.class);
        this.context.startActivity(intent);
    }
}
