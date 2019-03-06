package com.example.jill.firsttry.activity;

import android.app.Activity;
import android.media.Image;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jill.firsttry.R;

/**
 * 进入准备界面：录歌或试听
 */
public class RecordPrepareActivity extends Activity {
    private Button listenButton;
    private Button recordButton;
    private TextView Singer;
    private TextView songName;
    private ImageView songIcon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acticity_prepare_record);
        initComponent(); //绑定所有的组件

//        phoneText = findViewById(R.id.login_account);
//        Button getVerify = findViewById(R.id.login_btn);
//        getVerify.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                String phone = phoneText.getText().toString().trim();
//                if (!TextUtils.isEmpty(phone) && isPhoneNum(phone)) {
//                    postRequest(phone);//post
//                } else {
//                    Toast.makeText(LoginAcitivity.this, "输入手机号格式不匹配", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
    }

    /**
     * 绑定界面组件
     */
    private void initComponent(){
        // 绑定按钮
        listenButton =  findViewById(R.id.prepare_button_listen);
        recordButton = findViewById(R.id.prepare_button_record);
        // 绑定text
        songName = findViewById(R.id.prepare_text_singer);
        songName = findViewById(R.id.prepare_text_songName);
        //image
        songIcon = findViewById(R.id.prepare_image_songIcon);
    }
}
