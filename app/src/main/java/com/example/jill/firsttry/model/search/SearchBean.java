package com.example.jill.firsttry.model.search;

import com.example.jill.firsttry.model.Song;

import java.util.ArrayList;

public class SearchBean {
    public String statusCode;
    public String statusExpression;
    public ArrayList<Song> data;

    public ArrayList<Song> getData() {
        return data;
    }

    public void setData(ArrayList<Song> data) {
        this.data = data;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusExpression() {
        return statusExpression;
    }

    public void setStatusExpression(String statusExpression) {
        this.statusExpression = statusExpression;
    }
}

