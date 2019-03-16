package com.example.jill.firsttry.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jill.firsttry.R;
import com.example.jill.firsttry.Utils.Consts;
import com.example.jill.firsttry.Utils.HttpUtil;
import com.example.jill.firsttry.forLyrics.LyricsReader;
import com.example.jill.firsttry.forLyrics.utils.ColorUtils;
import com.example.jill.firsttry.forLyrics.utils.TimeUtils;
import com.example.jill.firsttry.forLyrics.widget.AbstractLrcView;
import com.example.jill.firsttry.forLyrics.widget.ManyLyricsView;
import com.example.jill.firsttry.model.Song;
import com.example.jill.firsttry.model.global_val.AppContext;
import com.zml.libs.widget.MusicSeekBar;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 试听界面
 */
public class NewPlayActivity extends AppCompatActivity {

    /**
     * 多行歌词视图
     */
    private ManyLyricsView mManyLyricsView;
    /**
     * 歌曲播放进度
     */
    private TextView mSongProgressTV;
    /**
     * 歌曲进度条
     */
    private MusicSeekBar mMusicSeekBar;

    /**
     * 歌曲长度
     */
    private TextView mSongDurationTV;
    /**
     * 播放按钮
     */
    private Button mPlayBtn;
    /**
     * 暂停按钮
     */
    private Button mPauseBtn;
    /**
     * 停止按钮
     */
    private Button mStopBtn;

    private Button mTranslateBtn;
    /**
     * 播放器
     */
    private MediaPlayer mMediaPlayer;

    /**
     * 更新进度
     */
    private final int UPDATE_PROGRESS = 0;

    /**
     * 额外歌词回调
     */
    private final int EXTRALRCALLBACK = 1;

    /**
     * 播放歌曲
     */
    private final int MUSIC_PLAY = 2;

    /**
     * 歌曲暂停
     */
    private final int MUSIC_PAUSE = 3;

    /**
     * 歌曲初始
     */
    private final int MUSIC_INIT = 4;
    /**
     * 歌曲快进
     */
    private final int MUSIC_SEEKTO = 5;
    /**
     * 歌曲停止
     */
    private final int MUSIC_STOP = 6;
    /**
     * 歌曲唤醒
     */
    private final int MUSIC_RESUME = 7;

    public static final int GET_DATA_SUCCESS = 8;
    public static final int NETWORK_ERROR = 9;
    public static final int SERVER_ERROR = 10;

    //从准备界面传回的song
    private Song currentSong;

    //private final String TAG = FloatActivity.class.getName();

    final OkHttpClient client = new OkHttpClient();

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_PROGRESS:

                    mMusicSeekBar.setEnabled(true);
                    if (mMediaPlayer != null) {
                        if (mMusicSeekBar.getMax() == 0) {
                            mMusicSeekBar.setMax(mMediaPlayer.getDuration());
                            mSongDurationTV.setText(TimeUtils.parseMMSSString(mMediaPlayer.getDuration()));
                        }
                        //
                        mMusicSeekBar.setProgress(mMediaPlayer.getCurrentPosition());
                        mSongProgressTV.setText(TimeUtils.parseMMSSString(mMediaPlayer.getCurrentPosition()));
                    }

                    break;

                case MUSIC_PLAY:
                    if (mMediaPlayer != null && mMediaPlayer.isPlaying() && mManyLyricsView.getLrcStatus() == AbstractLrcView.LRCSTATUS_LRC && mManyLyricsView.getLrcPlayerStatus() != AbstractLrcView.LRCPLAYERSTATUS_PLAY) {
                        mManyLyricsView.play(mMediaPlayer.getCurrentPosition());
                    }
                    break;

                case MUSIC_PAUSE:

                    if (mMediaPlayer != null && mManyLyricsView.getLrcStatus() == AbstractLrcView.LRCSTATUS_LRC) {
                        mManyLyricsView.pause();
                    }
                    break;

                case MUSIC_INIT:
                    mManyLyricsView.initLrcData();
                    //加载中
                    mManyLyricsView.setLrcStatus(AbstractLrcView.LRCSTATUS_LOADING);
                    break;

                case MUSIC_SEEKTO:
                    if (mMediaPlayer != null && mManyLyricsView.getLrcStatus() == AbstractLrcView.LRCSTATUS_LRC) {
                        mManyLyricsView.seekto(mMediaPlayer.getCurrentPosition());
                    }

                    break;

                case MUSIC_STOP:

                    mManyLyricsView.initLrcData();
                    //
                    mMusicSeekBar.setProgress(0);
                    mMusicSeekBar.setMax(0);
                    mMusicSeekBar.setEnabled(false);
                    //
                    mSongDurationTV.setText(TimeUtils.parseMMSSString(0));
                    mSongProgressTV.setText(TimeUtils.parseMMSSString(0));
                    finish();

                    break;

                case MUSIC_RESUME:

                    if (mMediaPlayer != null && mManyLyricsView.getLrcStatus() == AbstractLrcView.LRCSTATUS_LRC) {
                        mManyLyricsView.resume();
                    }

                    break;
                case NETWORK_ERROR:
                    Toast.makeText(NewPlayActivity.this, "网络不好", Toast.LENGTH_LONG).show();


            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_play);

        currentSong = (Song) getIntent().getSerializableExtra("song_data_from_recordPrepare");

        //
        mManyLyricsView = findViewById(R.id.manyLyricsView);
        //默认颜色
        int[] paintColors = new int[]{
                ColorUtils.parserColor("#ffffff"),
                ColorUtils.parserColor("#ffffff")
        };
        mManyLyricsView.setPaintColor(paintColors, false);

        //高亮颜色
        int[] paintHLColors = new int[]{
                ColorUtils.parserColor("#fada83"),
                ColorUtils.parserColor("#fada83")
        };
        mManyLyricsView.setPaintHLColor(paintHLColors, false);

        mManyLyricsView.setExtraLyricsListener(new AbstractLrcView.ExtraLyricsListener() {
            @Override
            public void extraLrcCallback() {
                mHandler.sendEmptyMessage(EXTRALRCALLBACK);
            }
        });

        //
        mManyLyricsView.setSearchLyricsListener(new AbstractLrcView.SearchLyricsListener() {
            @Override
            public void goToSearchLrc() {

            }
        });
        mManyLyricsView.setOnLrcClickListener(new ManyLyricsView.OnLrcClickListener() {
            @Override
            public void onLrcPlayClicked(int progress) {
                if (mMediaPlayer != null) {
                    if (progress <= mMediaPlayer.getDuration()) {
                        mMediaPlayer.seekTo(progress);
                    }
                }

            }
        });
        //
        mSongProgressTV = findViewById(R.id.songProgress);
        mMusicSeekBar = findViewById(R.id.lrcseekbar);
        mMusicSeekBar.setOnMusicListener(new MusicSeekBar.OnMusicListener() {
            @Override
            public String getTimeText() {
                return TimeUtils.parseMMSSString(mMusicSeekBar.getProgress());
            }

            @Override
            public String getLrcText() {
                return null;
            }

            @Override
            public void onProgressChanged(MusicSeekBar musicSeekBar) {

            }

            @Override
            public void onTrackingTouchFinish(MusicSeekBar musicSeekBar) {
                if (mMediaPlayer != null) {
                    mMediaPlayer.seekTo(mMusicSeekBar.getProgress());
                }
            }
        });


        mSongDurationTV = findViewById(R.id.songDuration);

        //
        mPlayBtn = findViewById(R.id.play);
        mPlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mMediaPlayer == null) {

                    mHandler.sendEmptyMessage(MUSIC_INIT);

                    //
                    //mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.aiqingyu);
                    mMediaPlayer = new MediaPlayer();
                    // String fileUrl="http://58.87.73.51/musicshop/"+app.getDownloadBean().getData().getFilePath();
                    String fileUrl = Consts.ENDPOINT + currentSong.getFilePath();
                    mMediaPlayer.reset();
                    try {
                        mMediaPlayer.setDataSource(fileUrl);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            mMediaPlayer.release();
                            mMediaPlayer = null;

                            mHandler.sendEmptyMessage(MUSIC_STOP);
                        }
                    });

                    //快进事件
                    mMediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                        @Override
                        public void onSeekComplete(MediaPlayer mediaPlayer) {
                            mHandler.sendEmptyMessage(MUSIC_SEEKTO);
                        }
                    });


                    //异步加载歌词文件


                    try {
                        mMediaPlayer.prepare();
                        loadLrcFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    mMediaPlayer.start();
                    mHandler.sendEmptyMessage(MUSIC_PLAY);
                    mHandler.postDelayed(mPlayRunnable, 0);

                    return;
                }

                if (mMediaPlayer.isPlaying()) return;

                mMediaPlayer.start();
                //play();
                // mMediaPlayer.start();
                mHandler.sendEmptyMessage(MUSIC_RESUME);
                mHandler.postDelayed(mPlayRunnable, 0);
            }
        });

        mPauseBtn = findViewById(R.id.pause);
        mPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMediaPlayer.pause();
                mHandler.sendEmptyMessage(MUSIC_PAUSE);
            }
        });

        mStopBtn = findViewById(R.id.stop);
        mStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMediaPlayer != null) {
                    mMediaPlayer.stop();
                    mMediaPlayer.release();
                    mMediaPlayer = null;
                }

                mHandler.sendEmptyMessage(MUSIC_STOP);

            }
        });

    }

    /**
     * 加载歌词文件
     */
    private void loadLrcFile() {
        HttpUtil.sendOkHttpRequest(Consts.ENDPOINT + currentSong.getLyric(), new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                mHandler.sendEmptyMessage(NETWORK_ERROR);
                Log.d("NETERROR", "出错了");
                mManyLyricsView.setLrcStatus(AbstractLrcView.LRCSTATUS_ERROR);
            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                if (response.code() == Consts.RESPONSE_OK) {
                    //Log.d("NETERROR", "没出错" + Consts.ENDPOINT + currentSong.getLyric());
                    try {
                        LyricsReader lyricsReader = new LyricsReader();
                        byte[] data = response.body().bytes();
                        lyricsReader.loadLrc(data,null,Consts.Cache_DIR+currentSong.getSname()+"-"+currentSong.getSingerName()+"-"+currentSong.getAlbum()+"-"+currentSong.getSid()+".krc");
                        mManyLyricsView.setLyricsReader(lyricsReader);
                        if (mMediaPlayer != null && mMediaPlayer.isPlaying() && mManyLyricsView.getLrcStatus() == AbstractLrcView.LRCSTATUS_LRC && mManyLyricsView.getLrcPlayerStatus() != AbstractLrcView.LRCPLAYERSTATUS_PLAY) {
                            mManyLyricsView.play(mMediaPlayer.getCurrentPosition());
                        }
                    } catch (NullPointerException e) {
                        mManyLyricsView.setLrcStatus(AbstractLrcView.LRCSTATUS_ERROR);
                        e.printStackTrace();
                    } catch (Exception e) {
                        mManyLyricsView.setLrcStatus(AbstractLrcView.LRCSTATUS_ERROR);
                        e.printStackTrace();
                    }
                } else {
                    //Log.d("NETERROR", "有response但出错" + Consts.ENDPOINT + currentSong.getLyric());
                    mManyLyricsView.setLrcStatus(AbstractLrcView.LRCSTATUS_ERROR);
                }
            }
        });
    }

    private Runnable mPlayRunnable = new Runnable() {
        @Override
        public void run() {
            if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                mHandler.sendEmptyMessage(UPDATE_PROGRESS);

                mHandler.postDelayed(mPlayRunnable, 1000);
            }
        }
    };

    @Override
    public void onBackPressed() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        super.onDestroy();
    }


    public void play() {
        try {
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mMediaPlayer.start();
                }
            });
            mMediaPlayer.prepareAsync();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void actionStart(Context context, Song song) {
        Intent intent = new Intent(context, NewPlayActivity.class);
        // 在Intent中传递数据
        intent.putExtra("song_data_from_recordPrepare", song);
        // 启动Intent
        context.startActivity(intent);
    }

}

