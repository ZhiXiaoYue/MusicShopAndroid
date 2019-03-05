package com.example.jill.firsttry.others;


/**
 * Created by seceinfofs on 2016/12/15.
 */

public class ResponseBean {
    private String version;
    private DataBean data;

    public void setVersion(String version) {
        this.version = version;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getVersion() {
        return version;
    }

    public DataBean getData() {
        return data;
    }

    class DataBean{
        String code;
        String uid;
        String sid;
        String token;


        public void setSid(String sid) {
            this.sid = sid;
        }

        public String getSid() {
            return sid;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getCode() {
            return code;
        }

        public String getToken() {
            return token;
        }

        public String getUid() {
            return uid;
        }
    }
}