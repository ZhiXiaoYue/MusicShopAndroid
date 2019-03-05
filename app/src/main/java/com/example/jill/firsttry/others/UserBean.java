package com.example.jill.firsttry.others;

public class UserBean {
    private String statusCode;
    private  String data;
    private String name;
    private String statusExpression;
    private String phone;

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getData() {
        return data;
    }

    public String getName() {
        return name;
    }

    public void setData(String data) {
        this.data = data;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getStatusExpression() {
        return statusExpression;
    }

    public void setStatusExpression(String sign) {
        this.statusExpression = sign;
    }
}
