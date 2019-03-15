package com.example.jill.firsttry.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.example.jill.firsttry.R;
import com.example.jill.firsttry.Utils.ImageUtil;
import com.example.jill.firsttry.model.Song;

/**
 * 进入准备界面：录歌或试听
 */
public class RecordPrepareActivity extends Activity {
    private Button listenButton;
    private Button recordButton;
    private TextView singer;
    private TextView songName;
    private ImageUtil songIcon;

    public RecordPrepareActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Song currentSong;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acticity_prepare_record);
        initComponent(); //绑定所有的组件
        //currentSong = testWithFakeData(); // 测试的时候装一点假数据，后来删掉就改行+testWithFakeData函数+取消下一行的注释
        //从searchctivity获取用户选择的歌曲
         currentSong = (Song) getIntent().getSerializableExtra("song_data_from_search");
        //设置界面信息
        singer.setText(currentSong.getSingerName());
        songName.setText(currentSong.getSname());
        songIcon.setImageURL("http://58.87.73.51:8080/musicshop/"+currentSong.getAlbumPic());
        // 点击试听
        listenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
               NewPlayActivity.actionStart(RecordPrepareActivity.this,currentSong);
            }
        });
        recordButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecordPrepareActivity.this, NewPlayActivity.class);
                // 在Intent中传递数据
                intent.putExtra("song_data_from_recordPrepare", currentSong);
                // 启动Intent
                startActivity(intent);
            }
        });
    }

    /**
     * 绑定界面组件
     */
    private void initComponent(){
        // 绑定按钮
        listenButton =  findViewById(R.id.prepare_button_listen);
        recordButton = findViewById(R.id.prepare_button_record);
        // 绑定text
        singer = findViewById(R.id.prepare_text_singer);
        songName = findViewById(R.id.prepare_text_songName);
        //image
        songIcon = findViewById(R.id.prepare_image_songIcon);
    }
    /**
     * 装一点假的数据，后来删掉就好
     */
    private Song testWithFakeData(){
        Song song = new Song();
        song.setSname("原谅（Cover张玉华");
        song.setSingerName("刘瑞琦");
        song.setSid(40);
        song.setAlbum("头号粉丝");
        song.setAlbumPic("static/album_thumbnails/刘瑞琦-头号粉丝.jpg");
        song.setFilePath("static/music/原谅（Cover张玉华）-刘瑞琦.mp3");
        song.setInstrumental("static/instru/原谅（Cover张玉华）刘瑞琦.mp3");
        song.setLyric("static/lyric/原谅（Cover张玉华）刘瑞琦.krc");
        return song;
    }

    /**
     * 进入该界面需要传入Song对象
     * @param context 代表别的界面的上下文context
     */
    public static void actionStart(Context context,Song song){
        Intent intent=new Intent(context,RecordPrepareActivity.class);
        intent.putExtra("song_data_from_search",song);
        context.startActivity(intent);
    }
}
