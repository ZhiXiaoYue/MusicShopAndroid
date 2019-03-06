package com.example.jill.firsttry.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.net.MalformedURLException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@SuppressLint("AppCompatCustomView")
public class ImageUtil extends ImageView {
    public static final int GET_DATA_SUCCESS = 1;
    public static final int NETWORK_ERROR = 2;
    public static final int SERVER_ERROR = 3;
    final OkHttpClient client = new OkHttpClient();
    public ImageUtil(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ImageUtil(Context context) {
        super(context);
    }

    public ImageUtil(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //子线程不能操作UI，通过Handler设置图片
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case GET_DATA_SUCCESS:
                    byte[] Picture = (byte[]) msg.obj;
                    //使用BitmapFactory工厂,把字节数组转换为bitmap
                    Bitmap bitmap = BitmapFactory.decodeByteArray(Picture, 0, Picture.length);
                    //通过ImageView,设置图片
                    setImageBitmap(bitmap);
                    break;
                case NETWORK_ERROR:
                    Toast.makeText(getContext(),"网络连接失败",Toast.LENGTH_SHORT).show();
                    break;
                case SERVER_ERROR:
                    Toast.makeText(getContext(),"服务器发生错误",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    //设置网络图片
    public void setImageURL(final String path) {
        final Request request = new Request.Builder()
                .url(path)
                .build();
        //开启一个线程用于联网
        new Thread() {
            @Override
            public void run() {
                try {
                    Response response;
                        //回调
                    response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        // 下载图片
                        assert response.body() != null;
                        byte[] Picture_bt = response.body().bytes();
                        //通过handler更新UI
                        Message message = handler.obtainMessage();
                        message.obj = Picture_bt;
                        message.what = GET_DATA_SUCCESS;
                        handler.sendMessage(message);
                    } else {
                        //服务启发生错误
                        handler.sendEmptyMessage(SERVER_ERROR);
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                    //网络连接错误
                    handler.sendEmptyMessage(NETWORK_ERROR);
                }
            }
        }.start();
    }
}
