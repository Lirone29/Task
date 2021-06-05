package com.example.task.models;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.ContentValues.TAG;

public class Lists {
    public Integer id;
    public String title;
    public Integer userId;

    public Lists() {
    }

    public Lists(Integer id, String title, Integer userId) {
        this.id = id;
        this.title = title;
        this.userId = userId;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getId() {
        return id;
    }

    public Integer getUserId() {
        return userId;
    }

    public JSONObject getJSONObject() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("id",id);
            obj.put("title", title);
            obj.put("userId", userId);
        } catch (JSONException e) {
            Log.d(TAG, "DefaultListItem.toString JSONException: "+e.getMessage());
        }
        return obj;
    }
}
