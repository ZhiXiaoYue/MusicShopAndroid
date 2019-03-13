package com.example.jill.firsttry.Utils;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.example.jill.firsttry.activity.BaseActivity;
import com.example.jill.firsttry.model.Base;
import com.example.jill.firsttry.model.response.BaseResponse;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

import static com.example.jill.firsttry.Utils.SharedPreferencesUtil.TAG;

public abstract class callListenner extends callBackImp  {

    private BaseActivity activity;
    private BaseResponse baseResponse;

    public callListenner(BaseActivity activity){
        this.activity=activity;
    }

    public abstract void onSucceeded(Call call,BaseResponse baseResponse) throws IOException ;

    @Override
    public void onFailure(Call call, IOException e) {
        super.onFailure(call,e);
        onFailed(null,e);
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        super.onResponse(call,response);
        baseResponse=getBaseResponse(response);
        if(isSuccess(response)){
            onSucceeded(call,baseResponse);
        }else {
            System.out.println(response.body().toString());
            onFailed(response,null);
        }
    }

    public void onFailed(Response response, Throwable e) {
//        if (activity != null) {
//            activity.hideLoading();
//        }
        if (e != null) {
            new ExceptHandler(activity).handle(e);
        } else {
            if (response != null && baseResponse!=null) {
                {
                    if (TextUtils.isEmpty(baseResponse.getStatusExpression())){
                    new ExceptHandler(activity).handle(baseResponse);
                    }
                }
            } else {
                ToastUtil.showSortToast(activity, "未知错误,请稍后再试!");
            }
        }
    }

    public boolean isSuccess(Response response) {
        return (response.code()==200&&(baseResponse.getStatusCode()).equals("200"));
    }

    public static BaseResponse getBaseResponse(Response response){
        String responseString = response.body().toString();
//        responseString=responseString.replaceAll("\"\\[","\\[");
//        responseString=responseString.replaceAll("\\]\"","\\]");
//        responseString=responseString.replaceAll("\\\\\"","\"");
//        System.out.println(responseString);
        System.out.println(responseString);
        Log.d(TAG, "获取的结果是"+responseString);
       BaseResponse baseResponse=new Gson().fromJson(responseString,BaseResponse.class);
       return baseResponse;
    }
}
