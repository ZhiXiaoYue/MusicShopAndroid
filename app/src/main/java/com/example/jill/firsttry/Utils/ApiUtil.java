package com.example.jill.firsttry.Utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import com.example.jill.firsttry.activity.LoginAcitivity;
import com.example.jill.firsttry.model.Song;
import com.example.jill.firsttry.model.global_val.AppContext;
import com.example.jill.firsttry.model.response.BaseResponse;
import com.example.jill.firsttry.model.search.SearchBean;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

public class ApiUtil {

    /**
     * 根据sid查询歌曲信息
     */
    public Song getSongById(String sid, final Activity activity) {
        if (((AppContext) activity.getApplication()).getUser() == null) {
            Log.d("现在的token", "token是null");
        } else {
            Log.d("现在的token", ((AppContext) activity.getApplication()).getUser().getData());
        }
        final Song[] song=new Song[1];
        HttpUtil.sendOkHttpRequestWithHeader(Consts.ENDPOINT + "api/search?keyword=" + sid + "&type=" + Consts.SEARCH_TYPE_FOR_SID, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                //Toast.makeText(MainActivity.this,"failed",Toast.LENGTH_SHORT);
                System.out.println("失败了" + e.toString());
                Log.d(TAG, "失败了 " + e.toString());
                activity.runOnUiThread(
                        new Runnable() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void run() {
                                Toast.makeText(activity, "网络不太好哦", Toast.LENGTH_SHORT);
                            }
                        }
                );
            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                try {
                    String responseString = response.body().string();
                    Log.d("search返回的是", responseString);
                    Log.d("search返回的code是", (new Integer(response.code())).toString());
                    if (response.code() == 200) {
                        if (isLogin(responseString)) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    needLogin(activity);
                                }
                            });
                        } else {
                            if (getBaseResponse(responseString).getData().equals("null")) {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(activity, "未搜索到相关资源", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                responseString = responseString.replaceAll("\"\\[", "\\[");
                                responseString = responseString.replaceAll("\\]\"", "\\]");
                                responseString = responseString.replaceAll("\\\\\"", "\"");
                                System.out.println(responseString);
                                Log.d(TAG, "获得请求数据" + responseString);
                               song[0]= searchAfter(responseString);
                            }
                        }
                    } else {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(activity, "网络出现错误了", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(TAG, "请求失败了 " + e.toString());
                }
            }
        }, "token", ((AppContext) activity.getApplication()).getUser());
        return song[0];
    }

    /**
     * 处理修剪后的搜索字符串
     *
     * @param tring
     */
    private Song searchAfter(final String tring) {
        Gson gson = new Gson();
        SearchBean searchBean = gson.fromJson(tring, SearchBean.class);
        return searchBean.getData().get(0);
    }

    /**
     * 用来装{"statusCode":,"statusExpression":,"data":}的
     */
    private BaseResponse getBaseResponse(String responseString) {
       Gson gson = new Gson();
        BaseResponse baseResponse = gson.fromJson(responseString, BaseResponse.class);
        return baseResponse;
    }

}
