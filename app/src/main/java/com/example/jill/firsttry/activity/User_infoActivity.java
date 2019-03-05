package com.example.jill.firsttry.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jill.firsttry.R;
import com.example.jill.firsttry.others.AppContext;
import com.example.jill.firsttry.others.UserBean;

public class User_infoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        final AppContext app = (AppContext )getApplication();
        UserBean user= app.getUser();
        TextView text_name = findViewById(R.id.user_name2);
        text_name.setText( user.getName());
        TextView text_sign = findViewById(R.id.user_sign);
        Button but_lOut= findViewById(R.id.button_logout);
        but_lOut.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new  AlertDialog.Builder(User_infoActivity.this)
                                .setTitle("确认" )
                                .setMessage("确定注销？" )
                                .setNegativeButton("是" ,   new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        app.setState(null);
                                        app.setUser(null);
                                        Toast.makeText(User_infoActivity.this, "注销成功", Toast.LENGTH_SHORT).show();
                                        Intent intent2 = new Intent(User_infoActivity.this,
                                                MainActivity.class);
                                        startActivity(intent2);
                                        finish();
                                    }
                                })
                                .setPositiveButton("否" ,  new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();
            }
        });
    }
}
