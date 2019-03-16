package com.example.jill.firsttry.model;

import java.util.ArrayList;

public class QueryRecordBean {
    private int statusCode;
    private String statusExpression;
    private String data;

    public QueryRecordBean() {
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusExpression() {
        return statusExpression;
    }

    public void setStatusExpression(String statusExpression) {
        this.statusExpression = statusExpression;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
