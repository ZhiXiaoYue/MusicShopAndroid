package com.example.jill.firsttry.model.response;

import com.example.jill.firsttry.model.Song;

import java.util.ArrayList;

/**
 * Created by smile on 03/03/2018.
 */

public class BaseResponse {

    public String statusCode;
    public String statusExpression;
    public String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
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
