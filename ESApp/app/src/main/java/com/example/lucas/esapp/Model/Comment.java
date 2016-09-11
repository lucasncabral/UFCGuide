package com.example.lucas.esapp.Model;

/**
 * Created by Carol on 11/09/2016.
 */
public class Comment {
    private String title, comment;

    public Comment() {
    }

    public Comment(String title, String comment) {
        this.title = title;
        this.comment = comment;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
