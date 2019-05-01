package com.example.jill.firsttry.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.webkit.DownloadListener;
import android.widget.Button;
import android.widget.TextView;

import com.example.jill.firsttry.R;
import com.example.jill.firsttry.Utils.Consts;
import com.example.jill.firsttry.Utils.DownloadRecordUtil;
import com.example.jill.firsttry.Utils.DownloadUtil;
import com.example.jill.firsttry.Utils.ImageUtil;
import com.example.jill.firsttry.model.Song;
import com.example.jill.firsttry.model.UserRecord;

import java.io.File;
import java.io.IOException;

/**
 * 进入准备界面：下载
 */
public class DownloadPrepareActivity extends Activity {
    private Button downloadButton;
    private TextView singer;
    private TextView songName;
    private ImageUtil songIcon;
    private Song currentSong;
    private UserRecord userRecord;
    public DownloadPrepareActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acticity_prepare_download);
        initComponent(); //绑定所有的组件

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());

        //currentSong = testWithFakeData(); // 测试的时候装一点假数据，后来删掉就改行+testWithFakeData函数+取消下一行的注释
        //从searchctivity获取用户选择的歌曲
        currentSong = (Song) getIntent().getSerializableExtra("song_data_from_main");
        userRecord = (UserRecord) getIntent().getSerializableExtra("url_data_from_main");
        //设置界面信息
        singer.setText(currentSong.getSingerName());
        songName.setText(currentSong.getSname());
        songIcon.setImageURL("http://58.87.73.51:8080/musicshop/" + currentSong.getAlbumPic());
        // 点击试听
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2019/4/30 下载歌曲，下载完成后跳转播放,url为recordUrl
                //如果伴奏存在就不去下载了
                if(!(new File(Consts.SONG_DIR+currentSong.getSname() + "-" + currentSong.getSingerName() + "-" + currentSong.getAlbum() + "-" + currentSong.getSid() + ".mp3").exists())){
                    download();
                }
                //下载原声录音
                downloadRecord();
            }
        });
    }

    /**
     * 下载伴奏和歌词
     */
    private void download() {
        DownloadRecordUtil downloadUtil = new DownloadRecordUtil(DownloadPrepareActivity.this,currentSong,userRecord);
        String lyricName = currentSong.getSname() + "-" + currentSong.getSingerName() + "-" + currentSong.getAlbum() + "-" + currentSong.getSid() + ".krc";
        String companyName = currentSong.getSname() + "-" + currentSong.getSingerName() + "-" + currentSong.getAlbum() + "-" + currentSong.getSid() + ".mp3";
        try {
            downloadUtil.download(Consts.ENDPOINT + currentSong.getLyric(), Consts.SONG_DIR, lyricName,Consts.LYRIC);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            downloadUtil.download(Consts.ENDPOINT + currentSong.getInstrumental(), Consts.SONG_DIR, companyName,Consts.COMPANY);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 下载录音
     */
    private void downloadRecord() {
        DownloadRecordUtil downloadUtil = new DownloadRecordUtil(DownloadPrepareActivity.this,currentSong,userRecord);
        String recordName = currentSong.getSname() + "-" + currentSong.getSingerName() + "-" + currentSong.getAlbum() + "-" + currentSong.getSid()+"-"+userRecord.getTime() + ".mp3";
        try {
            downloadUtil.download(Consts.ENDPOINT + currentSong.getInstrumental(), Consts.SAVE_SONG_DIR, recordName,Consts.ORIGINAL_RECORD);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 绑定界面组件
     */
    private void initComponent() {
        // 绑定按钮
        downloadButton = findViewById(R.id.button_download);
        // 绑定text
        singer = findViewById(R.id.prepare_text_singer);
        songName = findViewById(R.id.prepare_text_songName);
        //image
        songIcon = findViewById(R.id.prepare_image_songIcon);
    }


    /**
     * 进入该界面需要传入Song对象
     *
     * @param context 代表别的界面的上下文context
     */
    public static void actionStart(Context context, Song song, UserRecord userRecord) {
        Intent intent = new Intent(context, DownloadPrepareActivity.class);
        intent.putExtra("song_data_from_main", song);
        intent.putExtra("url_data_from_main", userRecord);
        context.startActivity(intent);
    }
}
