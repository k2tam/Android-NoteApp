package com.example.noteapp;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Note implements Serializable{
    private String noteID;
    private int priority;
    private String title;
    private String content;
    private String password;
    private Boolean lock;

    public Note(){}

    public Note(String noteID, int priority, String title, String content,String password, Boolean lock) {
        this.noteID = noteID;
        this.priority = priority;
        this.title = title;
        this.content = content;
        this.password = password;
        this.lock = false;
    }

    public int getPriority() {
        return priority;
    }

    public String getNoteID() {
        return noteID;
    }

    public void setNoteID(String noteID) {
        this.noteID = noteID;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getLock() {
        return lock;
    }

    public void setLock(Boolean lock) {
        this.lock = lock;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("noteID", noteID);
        result.put("priority", priority);
        result.put("title", title);
        result.put("content", content);
        result.put("password", password);
        result.put("lock", lock);
        return result;
    }

}
