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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Authentication extends AsyncTask<String, String, String>{
    public Context context;
    public static String[] userData = new String[8];

    public Authentication(Context context){
        this.context = context;
    }

    protected void onPreExecute(){
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... arg0) {
        try{
            String email = arg0[0];
            String password = arg0[1];

            String link="http://10.0.2.2:80/android_connect/www/authenticate.php";
            String data = URLEncoder.encode("email", "UTF-8") + "=" +
                    URLEncoder.encode(email, "UTF-8");
            data += "&" + URLEncoder.encode("password", "UTF-8") + "=" +
                    URLEncoder.encode(password, "UTF-8");

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
            HomeActivity.savedState = "home";
            Intent intent = new Intent(this.context, HomeActivity.class);
            try {
                loadIntoArray(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            this.context.startActivity(intent);
        }
    }

    private void loadIntoArray(String json) throws JSONException {
        JSONArray jsonArray = new JSONArray(json);
        JSONObject object = jsonArray.getJSONObject(0);
        userData[0] = object.getString("email");
        userData[1] = object.getString("password");
        userData[2] = object.getString("firstName");
        userData[3] = object.getString("rating");
        userData[4] = object.getString("gradeLevel");
        userData[5] = object.getString("courses");
        userData[6] = object.getString("favouriteTutors");
        userData[7] = object.getString("numberOfRatings");
    }

    private void goBack() {
        Intent intent = new Intent(this.context, LoginActivity.class);
        this.context.startActivity(intent);
    }
}
