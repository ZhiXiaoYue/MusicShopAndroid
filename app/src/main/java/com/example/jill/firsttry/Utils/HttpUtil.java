package com.example.jill.firsttry.Utils;

import android.util.Log;

import com.example.jill.firsttry.model.global_val.UserBean;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

/**
 * Created by 46639 on 2018/3/14.
 */

public class HttpUtil {
    public static Call sendOkHttpRequest(String address, okhttp3.Callback callback) {
        //OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(address)
                .build();
        OkHttpClient client=new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5,TimeUnit.SECONDS)
                .build();
        Call call=client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public static Call sendOkHttpRequestWithHeader(final String address, final okhttp3.Callback callback,
                                                   final String headerName,final UserBean userBean) {
        //OkHttpClient client = new OkHttpClient();
        Request request;
        if(userBean==null){
            Log.d("httputil","user是null");
            request= new Request.Builder()
                    .url(address)
                    .addHeader(headerName,"")
                    .build();
        }else {
            Log.d("httputil","user的token是"+userBean.getData());
            request = new Request.Builder()
                    .url(address)
                    .addHeader(headerName,userBean.getData())
                    .build();
        }
        OkHttpClient client=new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15,TimeUnit.SECONDS)
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

//    public static void postFile(String url,int sid,String token,final ProgressListener listener, Callback callback, File...files){
//        MediaType mediaType = MediaType.parse("multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW");
//        MultipartBody.Builder builder = new MultipartBody.Builder();
//        builder.setType(MultipartBody.FORM);
//        builder.setType(mediaType);
//        Log.i("huang","files[0].getName()=="+files[0].getName());
//        builder.addFormDataPart("file",files[0].getName());
//        builder.addFormDataPart("sid",Integer.valueOf(sid).toString());
//        builder.addFormDataPart("filename",files[0].getName(),(RequestBody.create(MediaType.parse("multipart/form-data"),files[0]));
//
//        MultipartBody multipartBody = builder.build();
//
//        Request request  = new Request.Builder()
//                .url(url)
//                .post(new ProgressRequestBody(multipartBody,listener))
//                .addHeader("token",token)
//                .build();
//        okHttpClient.newCall(request).enqueue(callback);
//    }
}