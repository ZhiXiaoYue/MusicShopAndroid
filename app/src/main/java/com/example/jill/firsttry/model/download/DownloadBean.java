package com.example.jill.firsttry.model.download;

public class DownloadBean {
    public String statusCode;
    public String statusExpression;
    public DownloadData data;

    public DownloadData getData() {
        return data;
    }

    public void setData(DownloadData data) {
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
