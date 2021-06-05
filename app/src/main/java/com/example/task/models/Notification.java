package com.example.task.models;

public class Notification {
    public Integer id;
    public Integer taskId;
    public String date;
    public String time;

    public Notification(Integer id, Integer taskId, String date, String time) {
        this.id = id;
        this.taskId = taskId;
        this.date = date;
        this.time = time;
    }


    public Notification(Integer taskId, String date, String time) {
        this.taskId = taskId;
        this.date = date;
        this.time = time;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
