package com.example.jill.firsttry.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.jill.firsttry.R;
import com.example.jill.firsttry.Utils.ApiUtil;
import com.example.jill.firsttry.model.Song;

public class AActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a);
        Button button=findViewById(R.id.button);
        final TextView textView=findViewById(R.id.textview);
        final ApiUtil api=new ApiUtil();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Song song=api.getSongById("31",AActivity.this);
                textView.setText(song.getSname());
            }
        });

    }
}
