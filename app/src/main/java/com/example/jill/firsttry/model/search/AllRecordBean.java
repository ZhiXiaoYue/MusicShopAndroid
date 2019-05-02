package com.example.jill.firsttry.model.search;

import com.example.jill.firsttry.model.Song;
import com.example.jill.firsttry.model.UserRecordSimple;

import java.util.ArrayList;

public class AllRecordBean {
    private String statusCode;
    private String statusExpression;
    public ArrayList<UserRecordSimple> data;

    public ArrayList<UserRecordSimple> getData() {
        return data;
    }

    public void setData(ArrayList<UserRecordSimple> data) {
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

