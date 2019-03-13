package com.example.jill.firsttry.Utils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by 46639 on 2018/3/14.
 */

public class HttpUtil {
    public static Call sendOkHttpRequest(final String address, final okhttp3.Callback callback) {
        //OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(address)
                .build();
        OkHttpClient client=new OkHttpClient.Builder()
                .connectTimeout(2, TimeUnit.MINUTES)
                .readTimeout(2,TimeUnit.MINUTES)
                .build();
        Call call=client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public static Call sendOkHttpRequestWithHeader(final String address, final okhttp3.Callback callback,
                                                   final String headerName,final String headerContent) {
        //OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(address)
                .addHeader(headerName,headerContent)
                .build();
        OkHttpClient client=new OkHttpClient.Builder()
                .connectTimeout(2, TimeUnit.MINUTES)
                .readTimeout(2,TimeUnit.MINUTES)
                .build();
        Call call=client.newCall(request);
        call.enqueue(callback);
        return call;
    }


    //使用Post方式向服务器上提交数据并获取返回提示数据
    public static void sendOkHttpResponse(final String address, final RequestBody requestBody, final okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(address).post(requestBody).build();
        client.newCall(request).enqueue(callback);
    }

    public static Response getOKhttp(final String address) throws IOException {
        OkHttpClient client =new OkHttpClient();
        Request request=new Request.Builder()
                .url(address)
                .build();
        return client.newCall(request).execute();
    }
}