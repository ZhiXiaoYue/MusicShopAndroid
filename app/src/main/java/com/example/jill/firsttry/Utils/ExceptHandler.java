package com.example.jill.firsttry.Utils;

import android.app.Activity;
import android.content.Intent;

import com.example.jill.firsttry.activity.BaseActivity;
import com.example.jill.firsttry.activity.LoginAcitivity;
import com.example.jill.firsttry.model.response.BaseResponse;


import java.net.ConnectException;
import java.net.SocketTimeoutException;

import retrofit2.HttpException;


/**
 * Created by smile on 03/03/2018.
 */

public class ExceptHandler {
    private BaseActivity activity;

    public ExceptHandler(BaseActivity activity) {
        this.activity = activity;
    }

    public void handle(Throwable e) {
        e.printStackTrace();
        if (e instanceof ConnectException) {
            ToastUtil.showSortToast(activity, "网络好像不太好呀!");
        }else if(e instanceof SocketTimeoutException){
            ToastUtil.showSortToast(activity, "连接超时,请稍后再试!");
        }else if (e instanceof HttpException){
            HttpException ex = (HttpException) e;
            if (ex.code() == 203) {
                //跳转到登陆页面
                activity.startActivity(new Intent(activity,LoginAcitivity.class));
                ToastUtil.showSortToast(activity, "请先登陆!");
                activity.finish();
            } else if (ex.code() == 403) {
                ToastUtil.showSortToast(activity, "你没有权限访问!");
            } else if (ex.code() == 404) {
                ToastUtil.showSortToast(activity, "你访问内容不存在!");
            } else if (ex.code() >= 500) {
                ToastUtil.showSortToast(activity, "服务器错误,请稍后再试!");
            } else {
                ToastUtil.showSortToast(activity, "未知错误,请稍后再试!");
            }
        } else{
            ToastUtil.showSortToast(activity, "未知错误,请稍后再试!");
        }
    }

    public void handle(BaseResponse baseResponse) {
        if (baseResponse.getStatusCode().equals("203")) {
            //跳转到登陆页面
             activity.startActivity(new Intent(activity,LoginAcitivity.class));
            ToastUtil.showSortToast(activity, "请先登陆!");
            activity.finish();
        }
    }

}