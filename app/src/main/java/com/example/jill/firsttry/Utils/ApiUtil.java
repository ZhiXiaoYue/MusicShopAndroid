package com.example.jill.firsttry.Utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import com.example.jill.firsttry.activity.LoginAcitivity;
import com.example.jill.firsttry.model.Song;
import com.example.jill.firsttry.model.global_val.AppContext;
import com.example.jill.firsttry.model.response.BaseResponse;
import com.example.jill.firsttry.model.search.AllRecordBean;
import com.example.jill.firsttry.model.search.SearchBean;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.content.ContentValues.TAG;
import com.example.jill.firsttry.model.UserRecord;
import com.example.jill.firsttry.model.global_val.AppContext;
import com.example.jill.firsttry.model.global_val.UserBean;
import com.example.jill.firsttry.model.response.BaseResponse;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ApiUtil {
    /**
     * 根据sid查询歌曲信息
     */
    
    private static final String TAG = "1";
    private static final int NETWORK_ERROR = 2;
    private static final int SERVER_ERROR = 3;
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
//                        if (isLogin(responseString)) {
//                            activity.runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    needLogin(activity);
//                                }
//                            });
//                        } else {
//                            if (getBaseResponse(responseString).getData().equals("null")) {
//                                activity.runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        Toast.makeText(activity, "未搜索到相关资源", Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//                            } else {
//                                responseString = responseString.replaceAll("\"\\[", "\\[");
//                                responseString = responseString.replaceAll("\\]\"", "\\]");
//                                responseString = responseString.replaceAll("\\\\\"", "\"");
//                                System.out.println(responseString);
//                                Log.d(TAG, "获得请求数据" + responseString);
//                               song[0]= searchAfter(responseString);
//                            }
//                        }
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

    public UserRecord getURById(String uRId, final Activity superActivity){
        final UserRecord[] userRecord = {new UserRecord()};
        @SuppressLint("HandlerLeak") final Handler mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                switch (msg.what){
                    case NETWORK_ERROR:
                        Toast.makeText(superActivity, "验证码发送失败，请检查网络连接", Toast.LENGTH_LONG).show();
                        break;
                    case SERVER_ERROR:
                        Toast.makeText(superActivity, "服务器发生错误，请联系客服", Toast.LENGTH_LONG).show();
                        break;
                }
                if(msg.what==1){
                    String returnMessage = (String) msg.obj;
                    BaseResponse br =  new Gson().fromJson(returnMessage, BaseResponse.class);
                    String A = br.getStatusExpression();
                    if(A.equals("Success")){
                        returnMessage = br.getData();
                        returnMessage= returnMessage.replaceAll("\\\\\"", "\"");
                        UserRecord  ur =  new Gson().fromJson(returnMessage, UserRecord.class);
                        userRecord[0] = ur;
//                        System.out.println("测试成功"+ur.getFilepath());
                    }
                    else{
                        Toast.makeText(superActivity, "服务器发生错误，请联系客服", Toast.LENGTH_LONG).show();
                    }
                }
            }
        };
        final OkHttpClient client = new OkHttpClient();

        String mUrl = String.format("http://58.87.73.51:8080/musicshop/api/findbyrid?rid=%s",uRId);
        final Request request = new Request.Builder()
                .addHeader("token", getUserToken(superActivity))
                .url(mUrl)
                .build();
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                Response response;
                try {
                    //回调
                    response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        //将服务器响应的参数response.body().string())发送到hanlder中，并更新ui
                        mHandler.obtainMessage(1, Objects.requireNonNull(response.body()).string()).sendToTarget();
                    } else {
                        throw new IOException("Unexpected code:" + response);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return userRecord[0];
    }

    public AllRecordBean getAllRecord(final Activity superActivity){
        final AllRecordBean[] allRecordBeans = {new AllRecordBean()};
        @SuppressLint("HandlerLeak") final Handler mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                switch (msg.what){
                    case NETWORK_ERROR:
                        Toast.makeText(superActivity, "验证码发送失败，请检查网络连接", Toast.LENGTH_LONG).show();
                        break;
                    case SERVER_ERROR:
                        Toast.makeText(superActivity, "服务器发生错误，请联系客服", Toast.LENGTH_LONG).show();
                        break;
                }
                if(msg.what==1){
                    String returnMessage = (String) msg.obj;
                    returnMessage= returnMessage.replaceAll("\\\\\"", "\"");
                    returnMessage= returnMessage.replaceAll("\"\\[", "\\[");
                    returnMessage= returnMessage.replaceAll("\\]\"", "\\]");
                    AllRecordBean  arb =  new Gson().fromJson(returnMessage, AllRecordBean.class);
                    allRecordBeans[0] = arb;
                    String A = arb.getStatusExpression();
                    if(A.equals("Success")){
                        System.out.println("测试成功"+arb.getData().get(0).getFilepath());
                    }
                    else{
                        Toast.makeText(superActivity, "服务器发生错误，请联系客服", Toast.LENGTH_LONG).show();
                    }
                }
            }
        };
        final OkHttpClient client = new OkHttpClient();

        String mUrl = "http://58.87.73.51:8080/musicshop/api/queryrecord";
        final Request request = new Request.Builder()
                .addHeader("token", getUserToken(superActivity))
                .url(mUrl)
                .build();
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                Response response;
                try {
                    //回调
                    response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        //将服务器响应的参数response.body().string())发送到hanlder中，并更新ui
                        mHandler.obtainMessage(1, Objects.requireNonNull(response.body()).string()).sendToTarget();
                    } else {
                        throw new IOException("Unexpected code:" + response);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return allRecordBeans[0];

    }
    private String getUserToken(Activity activity){
        AppContext app = (AppContext)activity.getApplication();
        UserBean currentUser= app.getUser();
        if(currentUser == null)
            needLogin(activity);
        return app.getUser().getData();
    }

    private void needLogin(Activity activity) {
        Log.d(TAG, "请登录");
        Toast.makeText(activity, "请先登录", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(activity, LoginAcitivity.class);
        activity.startActivity(intent);
    }
}
