package com.example.jill.firsttry.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.jill.firsttry.Fragments.SearchSongResultFragment;
import com.example.jill.firsttry.R;
import com.example.jill.firsttry.Utils.Consts;
import com.example.jill.firsttry.model.global_val.AppContext;
import com.example.jill.firsttry.model.global_val.UserBean;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


@SuppressLint("Registered")
public class VerifyActivity  extends BaseCommonActivity {
    private EditText verifyCode;
    final OkHttpClient client = new OkHttpClient();
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            if(msg.what==1){
                String ReturnMessage = (String) msg.obj;
                Log.i("获取的返回信息",ReturnMessage);
                final AppContext app = (AppContext)getApplication();
                final UserBean user= app.getUser();
                final String phone = user.getPhone();
                final UserBean userBean = new Gson().fromJson(ReturnMessage, UserBean.class);
                final String A = userBean.getStatusExpression();
                userBean.setName(phone);
                userBean.setPhone(phone);
                /*
                 * 在此处可以通过获取到的Msg值来判断
                 * 给出用户提示注册成功 与否，以及判断是否用户名已经存在
                 */
                Log.i("MSG", A);
                if(A.equals("Success")){
                    app.setUser(userBean);
                    app.setState(A);
                    Toast.makeText(VerifyActivity.this, "登录成功", Toast.LENGTH_LONG).show();
                    File filedir = new File(Consts.DIR+ userBean.getPhone()+"/");
                    filedir.mkdir();
                    //判断是否是从其他界面跳转到登录界面，如果是则登录后要去其他界面
                    if(SearchSongResultFragment.SEARCH_BEFORE_LOGIN){
                        finish();
                    }else {
                        Intent intent = new Intent(VerifyActivity.this,
                                MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
                else{
                    Toast.makeText(VerifyActivity.this, "输入验证码有误", Toast.LENGTH_LONG).show();
                }
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
/*
 * 初始化
 */
        verifyCode =  findViewById(R.id.regist_nick);
        Button reg = findViewById(R.id.verify_btn);

        reg.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final AppContext app = (AppContext)getApplication();
                final UserBean user= app.getUser();
                String phone = user.getPhone();
                Log.i("MSG", phone);
                String nick = verifyCode.getText().toString().trim();
                Log.i("MSG", user.getData());
                String code = "{\"verifycode\":\""+nick+"\"}";
                Log.i("MSG2", code);
                if (code.equals(user.getData())){
                    postRequest(nick,phone);
                } else {
                        Toast.makeText(VerifyActivity.this, "输入验证码有误", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideKeyboard(v, ev)) {
                hideKeyboard(v.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }
    private boolean isShouldHideKeyboard(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0],
                    top = l[1],
                    bottom = top + v.getHeight(),
                    right = left + v.getWidth();
            return !(event.getX() > left) || !(event.getX() < right)
                    || !(event.getY() > top) || !(event.getY() < bottom);
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditText上，和用户用轨迹球选择其他的焦点
        return false;
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void hideKeyboard(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            Objects.requireNonNull(im).hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    /*/**
     * post请求后台
     * @param username
     * @param password
     */

    private void postRequest(String nick,String phone)  {
        int Uuid = (int) ((Math.random() * 9 + 1) * 100000);
        String UidString = Uuid + "";

        String mUrl = String.format("http://58.87.73.51:8080/musicshop/api/verify?verifycode=%s&mobile=%s&uuid=%s",nick,phone,UidString);
        Log.i("url", mUrl);
        final Request request = new Request.Builder()
                .url(mUrl)
                .build();


        //新建一个线程，用于得到服务器响应的参数
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
}
