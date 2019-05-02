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

import com.example.jill.firstry.event.OnSearchByIdFinishEvent;
import com.example.jill.firsttry.activity.LoginAcitivity;
import com.example.jill.firsttry.model.Song;
import com.example.jill.firsttry.model.global_val.AppContext;
import com.example.jill.firsttry.model.response.BaseResponse;
import com.example.jill.firsttry.model.search.SearchBean;
import com.google.gson.Gson;

import java.io.IOException;
import okhttp3.Response;
import com.example.jill.firsttry.model.UserRecord;

import org.greenrobot.eventbus.EventBus;

import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;


public class ApiUtil {
    /**
     * 根据sid查询歌曲信息
     */
    
    private static final String TAG = "1";
    private static final int NETWORK_ERROR = 2;
    private static final int SERVER_ERROR = 3;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void getSongById(String sid, final Activity superActivity) {
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
                    String responseString = (String) msg.obj;
                    BaseResponse br =  new Gson().fromJson(responseString, BaseResponse.class);
                    String A = br.getStatusExpression();
                    if(A.equals("Success")){
                        responseString = responseString.replaceAll("\"\\[", "\\[");
                        responseString = responseString.replaceAll("\\]\"", "\\]");
                        responseString = responseString.replaceAll("\\\\\"", "\"");
                        System.out.println(responseString);
                        Log.d(TAG, "获得请求数据" + responseString);
                       Song song= searchAfter(responseString);
                        EventBus.getDefault().post(new OnSearchByIdFinishEvent(song));
                        System.out.println(song.getSname());
                    }
                    else{
                        Toast.makeText(superActivity, "服务器发生错误，请联系客服", Toast.LENGTH_LONG).show();
                    }
                }
            }
        };

        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .addHeader("token", getUserToken(superActivity))
                .url(Consts.ENDPOINT + "api/search?keyword=" + sid+ "&type=" + Consts.SEARCH_TYPE_FOR_SID)
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
                        System.out.println("测试成功"+ur.getFilepath());
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

    private String getUserToken(Activity activity){
        AppContext app = (AppContext)activity.getApplication();
        return app.getUser().getData();
    }


}
