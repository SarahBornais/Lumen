package com.absolutepower.lumenapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CustomBaseAdapter extends BaseAdapter {
    private static ArrayList<JSONObject> questionsArray;

    private LayoutInflater mInflater;

    public CustomBaseAdapter (Context context, ArrayList<JSONObject> results) {
        questionsArray = results;
        mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return questionsArray.size();
    }

    public JSONObject getItem (int position) {
        return questionsArray.get(position);
    }

    public long getItemId (int position) {
        return position;
    }

    public View getView (int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item, null);
            holder = new ViewHolder();
            holder.textQuestionIndex = convertView.findViewById(R.id.questionIndex);
            holder.textQuestion = convertView.findViewById(R.id.question);
            holder.textCourse  = convertView.findViewById(R.id.course);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        try {
            holder.textQuestionIndex.setText(questionsArray.get(position).getString("questionIndex"));
            holder.textQuestion.setText(questionsArray.get(position).getString("question"));
            holder.textCourse.setText(questionsArray.get(position).getString("course") + " | " + questionsArray.get(position).getString("type"));
            return convertView;
        } catch (JSONException e) {
            return null;
        }
    }

    static class ViewHolder {
        TextView textQuestionIndex;
        TextView textQuestion;
        TextView textCourse;
    }
}
