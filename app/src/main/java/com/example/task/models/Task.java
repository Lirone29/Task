package com.example.task.models;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.ContentValues.TAG;

public class Task {
    public Integer id;
    public Integer listId;
    public String content;
    public Boolean accomplished;
    public Notification notification;


    public Task(Integer id, Integer listId, String content, Boolean accomplished) {
        this.id = id;
        this.listId = listId;
        this.content = content;
        this.accomplished = accomplished;
    }

    public Task() {
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getListId() {
        return listId;
    }

    public void setListId(Integer listId) {
        this.listId = listId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getAccomplished() {
        return accomplished;
    }

    public void setAccomplished(Boolean accomplished) {
        this.accomplished = accomplished;
    }

    public String getDate() {
        return this.notification.date;
    }

    public String getTime() {
        return this.notification.time;
    }

    public JSONObject getJSONObject() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("id",id);
            obj.put("listId", listId);
            obj.put("content", content);
            obj.put("accomplished", accomplished);
            obj.put("notificationId",notification.getId());
            obj.put("date",notification.getDate());
            obj.put("time",notification.getTime());
        } catch (JSONException e) {
            Log.d(TAG, "DefaultListItem.toString JSONException: "+e.getMessage());
        }
        return obj;
    }
}
