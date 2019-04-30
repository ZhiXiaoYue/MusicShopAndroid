package com.example.jill.firsttry.activity;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.jill.firsttry.R;
import com.example.jill.firsttry.forLyrics.LyricsReader;
import com.example.jill.firsttry.forLyrics.utils.ColorUtils;
import com.example.jill.firsttry.forLyrics.utils.TimeUtils;
import com.example.jill.firsttry.forLyrics.widget.AbstractLrcView;
import com.example.jill.firsttry.forLyrics.widget.ManyLyricsView;
import com.example.jill.firsttry.model.Song;
import com.example.jill.firsttry.model.global_val.AppContext;
import com.zml.libs.widget.MusicSeekBar;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * 试听我的歌曲（
 */
public class ListenActivity extends AppCompatActivity {


    private AppContext appContext;


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
    private MediaPlayer mMediaPlayerSound;

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

    //private final String TAG = FloatActivity.class.getName();

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_PROGRESS:

                    mMusicSeekBar.setEnabled(true);
                    if (mMediaPlayer != null&& mMediaPlayerSound!=null) {
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
                    if (mMediaPlayer != null && mMediaPlayerSound!=null&& mMediaPlayer.isPlaying()&&mMediaPlayerSound.isPlaying() && mManyLyricsView.getLrcStatus() == AbstractLrcView.LRCSTATUS_LRC && mManyLyricsView.getLrcPlayerStatus() != AbstractLrcView.LRCPLAYERSTATUS_PLAY) {
                        mManyLyricsView.play(mMediaPlayer.getCurrentPosition());
                    }
                    break;

                case MUSIC_PAUSE:

                    if (mMediaPlayer != null && mMediaPlayerSound!=null&&mManyLyricsView.getLrcStatus() == AbstractLrcView.LRCSTATUS_LRC) {
                        mManyLyricsView.pause();
                    }
                    break;

                case MUSIC_INIT:
                    mManyLyricsView.initLrcData();
                    //加载中
                    mManyLyricsView.setLrcStatus(AbstractLrcView.LRCSTATUS_LOADING);
                    break;

                case MUSIC_SEEKTO:
                    if (mMediaPlayer != null &&mMediaPlayerSound!=null&& mManyLyricsView.getLrcStatus() == AbstractLrcView.LRCSTATUS_LRC) {
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

                    break;

                case MUSIC_RESUME:

                    if (mMediaPlayer != null && mManyLyricsView.getLrcStatus() == AbstractLrcView.LRCSTATUS_LRC) {
                        mManyLyricsView.resume();
                    }

                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置状态栏可见；
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        setContentView(R.layout.activity_new_play);


        appContext = (AppContext)getApplication();



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
                    mMediaPlayerSound.seekTo(mMusicSeekBar.getProgress());
                }
            }
        });


        mSongDurationTV = findViewById(R.id.songDuration);

        //
        mPlayBtn = findViewById(R.id.play);
        mPlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mMediaPlayer == null || mMediaPlayerSound == null) {

                    mHandler.sendEmptyMessage(MUSIC_INIT);

                    //
                    //mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.aiqingyu);
                    Song keys=appContext.getSong();

                    mMediaPlayer=new MediaPlayer();
                    String fileUrl="/mnt/sdcard/MusicShopDownLoad/Songs/"+keys.getSname()+ "-" + keys.getSingerName() + "-" +keys.getAlbum()+"-"+keys.getSid()+".mp3";
                    mMediaPlayer.reset();

                    try {
                        mMediaPlayer.setDataSource(fileUrl);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

                    mMediaPlayerSound=new MediaPlayer();
                    String fileUrlSound="/mnt/sdcard/MusicShopDownLoad/MySongs/"+keys.getSname() + "-" + keys.getSingerName() + "-" +keys.getAlbum()+"-"+keys.getSid()+".mp3";
                    mMediaPlayerSound.reset();
                    try {
                        mMediaPlayerSound.setDataSource(fileUrlSound);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mMediaPlayerSound.setAudioStreamType(AudioManager.STREAM_MUSIC);

                    mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            mMediaPlayer.release();
                            mMediaPlayer = null;
                            mMediaPlayerSound.release();
                            mMediaPlayerSound=null;
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
                    loadLrcFile();

                    try {
                        mMediaPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    mMediaPlayer.setVolume(0.1f,0.1f);
                    mMediaPlayer.start();

                    try {
                        mMediaPlayerSound.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    mMediaPlayerSound.setVolume(1.0f,1.0f);
                    mMediaPlayerSound.start();

                    mHandler.sendEmptyMessage(MUSIC_PLAY);
                    mHandler.postDelayed(mPlayRunnable, 0);

                    return;
                }

                if (mMediaPlayer.isPlaying()) return;

                mMediaPlayer.start();
                mMediaPlayerSound.start();
                mHandler.sendEmptyMessage(MUSIC_RESUME);
                mHandler.postDelayed(mPlayRunnable, 0);
            }
        });

        mPauseBtn = findViewById(R.id.pause);
        mPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMediaPlayer.pause();
                mMediaPlayerSound.pause();
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
                    mMediaPlayerSound.stop();
                    mMediaPlayerSound.release();
                    mMediaPlayer = null;
                    mMediaPlayerSound=null;
                }

                mHandler.sendEmptyMessage(MUSIC_STOP);

            }
        });

    }

    /**
     * 加载歌词文件
     */
    private void loadLrcFile() {

        new AsyncTask<String, Integer, String>() {

            @Override
            protected String doInBackground(String... strings) {
                // InputStream inputStream = getResources().openRawResource(R.raw.aiqingyu_krc);
                Song keys=appContext.getSong();
                InputStream inputStream= null;
                try {
                    inputStream = new FileInputStream("/mnt/sdcard/MusicShopDownLoad/Songs/"+keys.getSname() + "-" + keys.getSingerName() + "-" +keys.getAlbum()+"-"+keys.getSid()+".krc");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    //延迟看一下加载效果
                    Thread.sleep(500);

                    LyricsReader lyricsReader = new LyricsReader();
                    byte[] data = new byte[inputStream.available()];
                    inputStream.read(data);
                    lyricsReader.loadLrc(data, null, "/mnt/sdcard/MusicShopDownLoad/Songs/"+keys.getSname() + "-" + keys.getSingerName() + "-" +keys.getAlbum()+"-"+keys.getSid()+".krc");
                    mManyLyricsView.setLyricsReader(lyricsReader);
                    //
                    if (mMediaPlayer != null && mMediaPlayer.isPlaying() && mManyLyricsView.getLrcStatus() == AbstractLrcView.LRCSTATUS_LRC && mManyLyricsView.getLrcPlayerStatus() != AbstractLrcView.LRCPLAYERSTATUS_PLAY) {
                        mManyLyricsView.play(mMediaPlayer.getCurrentPosition());
                    }

                    inputStream.close();
                } catch (Exception e) {

                    mManyLyricsView.setLrcStatus(AbstractLrcView.LRCSTATUS_ERROR);
                    //Log.e(TAG, e.toString());
                    e.printStackTrace();
                }
                inputStream = null;

                return null;
            }
        }.execute("");
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

    public void play(){
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

}
