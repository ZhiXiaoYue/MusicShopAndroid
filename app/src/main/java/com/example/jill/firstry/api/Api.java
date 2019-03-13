package com.example.jill.firstry.api;


import com.example.jill.firsttry.Utils.Consts;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by smile on 02/03/2018.
 */

public class Api {
    private static final String SONG_ID = "song_id";
    private static Api instance;
    private static Service service;

    Api() {

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(Consts.TIME_OUT, TimeUnit.SECONDS);//连接超时时间
        builder.writeTimeout(Consts.TIME_OUT, TimeUnit.SECONDS);//写操作 超时时间
        builder.readTimeout(Consts.TIME_OUT, TimeUnit.SECONDS);//读操作超时时间

//        if (LogUtil.isDebug) {
//            builder.addInterceptor(new HttpLoggingInterceptor());
//            builder.addNetworkInterceptor(new StethoInterceptor());
//            builder.addInterceptor(new ChuckInterceptor(AppContext.getContext()));
//        }

        //用对网络请求缓存，详细的查看《详解OKHttp》课程
        //builder.addInterceptor(FORCE_CACHE_NETWORK_DATA_INTERCEPTOR);
        //builder.addNetworkInterceptor(FORCE_CACHE_NETWORK_DATA_INTERCEPTOR);

        //公共请求参数
        builder.addNetworkInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                //SharedPreferencesUtil sp = SharedPreferencesUtil.getCurrentInstance();
                Request request = chain.request();
//                if (sp.isLogin()) {
//                    String userId = sp.getUserId();
//                    String token = sp.getToken();
//
//                    if (LogUtil.isDebug) {
//                        LogUtil.d("token:" + token + "," + userId);
//                    }
//
//                    request = chain.request().newBuilder()
//                            .addHeader("User", userId)
//                            .addHeader("Authorization", token)
//                            .build();
//                }
                return chain.proceed(request);
            }
        });

        // 添加公共参数拦截器


        Retrofit retrofit = new Retrofit.Builder()
                .client(builder.build())
                .baseUrl(Consts.ENDPOINT)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(Service.class);
    }

    public static Api getInstance() {
        if (instance == null) {
            instance = new Api();
        }
        return instance;
    }

    public static Api getInstanceWithHeader() {
        if (instance == null) {
            instance = new Api();
        }
        return instance;
    }


//    public Observable<ListResponse<Song>> searchSong(String title) {
//        HashMap<String, String> query = new HashMap<>();
//        query.put(Consts.TITLE,title);
//        return service.searchSong(query);
//    }


}
