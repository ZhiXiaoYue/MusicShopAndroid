package com.example.jill.firsttry.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.jill.firsttry.R;
import com.example.jill.firsttry.others.AppContext;
import com.example.jill.firsttry.others.UserBean;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginAcitivity extends Activity {
    private EditText phoneText;
//    private EditText verifyNum;

    final OkHttpClient client = new OkHttpClient();

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){

            if(msg.what==1){
                String ReturnMessage = (String) msg.obj;
                Log.i("获取的返回信息",ReturnMessage);
                final UserBean userBean = new Gson().fromJson(ReturnMessage, UserBean.class);
                final String A = userBean.getStatusExpression();
                final AppContext app = (AppContext)getApplication();
                /*
                 * 在此处可以通过获取到的Msg值来判断
                 * 给出用户提示注册成功与否，以及判断是否用户名已经存在
                 */
                Log.i("MSG", A);
                if(A.equals("Success")){
                    userBean.setName(phoneText.getText().toString().trim());
                    userBean.setPhone(phoneText.getText().toString().trim());
                    app.setState(A);//将返回值存入后台bean中
                    app.setUser(userBean);
                    Toast.makeText(LoginAcitivity.this, "登录成功", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(LoginAcitivity.this,
                            MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else if(A.equals("Need to Verify")){
                    userBean.setName(phoneText.getText().toString().trim());
                    userBean.setPhone(phoneText.getText().toString().trim());
                    app.setUser(userBean);
                    Toast.makeText(LoginAcitivity.this, "验证码已发送，请输入", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(LoginAcitivity.this,
                            VerifyActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    Toast.makeText(LoginAcitivity.this, "验证码发送失败，请检查手机号", Toast.LENGTH_LONG).show();
                }
            }
            else{
                Toast.makeText(LoginAcitivity.this, "验证码发送失败，请检查网络连接", Toast.LENGTH_LONG).show();
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        phoneText = findViewById(R.id.login_account);
        Button getVerify = findViewById(R.id.login_btn);
        getVerify.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String phone = phoneText.getText().toString().trim();
                if (!TextUtils.isEmpty(phone)  && isPhoneNum(phone)) {
                        postRequest(phone);//post
                    }
                else {
                    Toast.makeText(LoginAcitivity.this, "输入手机号格式不匹配", Toast.LENGTH_SHORT).show();
                }
            }
       });
//        verifyNum = findViewById(R.id.login_password);
//        String passwordStr = pwd.getText().toString();
//        Button buttonReg = findViewById(R.id.register_btn);
//        buttonReg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(LoginAcitivity.this,
//                        RegistActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        });
    }
    /*隐藏输入键盘*/
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

    /*判断是否为电话号码*/
    public boolean isPhoneNum(String phone) {
        String str = "^(13[0-9]|14[579]|15[0-3,5-9]|16[6]|17[0135678]|18[0-9]|19[89])\\d{8}$";
        //正则表达式
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(phone);
        //一个新的matcher判断是否匹配。
        return m.matches();
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

    /*
     * post请求后台
     * @param username
     * @param password
     */

    public void postRequest(String phone) {
        int Uuid = (int) ((Math.random() * 9 + 1) * 100000);
        String UidString = Uuid + "";
        @SuppressLint("DefaultLocale")
        String mUrl = String.format("http://58.87.73.51:8080/musicshop/api/login?mobile=%s&uuid=%s/", phone, UidString);
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

