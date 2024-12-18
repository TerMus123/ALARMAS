package com.example.a1app;

public class Alarm {
    private int id;
    private String time;
    private String message;

    public Alarm(int id, String time, String message) {
        this.id = id;
        this.time = time;
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public String getTime() {
        return time;
    }

    public String getMessage() {
        return message;
    }
}
