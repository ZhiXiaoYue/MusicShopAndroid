package com.example.jill.firsttry.activity;

        import android.annotation.SuppressLint;
        import android.app.AlertDialog;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.media.AudioManager;
        import android.media.MediaPlayer;
        import android.media.MediaRecorder;
        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.os.Handler;
        import android.os.Message;
        import android.support.v7.app.AppCompatActivity;
        import android.view.View;
        import android.widget.Button;
        import android.widget.TextView;

        import com.example.jill.firsttry.R;
        import com.example.jill.firsttry.Utils.Consts;
        import com.example.jill.firsttry.forLyrics.LyricsReader;
        import com.example.jill.firsttry.forLyrics.utils.ColorUtils;
        import com.example.jill.firsttry.forLyrics.utils.TimeUtils;
        import com.example.jill.firsttry.forLyrics.widget.AbstractLrcView;
        import com.example.jill.firsttry.forLyrics.widget.ManyLyricsView;
        import com.example.jill.firsttry.model.Song;
        import com.example.jill.firsttry.model.global_val.AppContext;
        import com.zml.libs.widget.MusicSeekBar;

        import java.io.File;
        import java.io.FileInputStream;
        import java.io.FileNotFoundException;
        import java.io.IOException;
        import java.io.InputStream;
        import java.text.SimpleDateFormat;
        import java.util.Date;

/**
 * 录音界面
 */
public class ManyActivity extends AppCompatActivity {

    private Song currentSong;

    private String time;

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
//    /**
//     * 暂停按钮
//     */
//    private Button mPauseBtn;
    /**
     * 停止按钮
     */
    private Button mStopBtn;

    private Button mTranslateBtn;
    /**
     * 播放器
     */
    private MediaPlayer mMediaPlayer;

    private MediaRecorder mMediaRecorder;

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

    @SuppressLint("HandlerLeak")
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
                    recordStop();
                    //
                    //mMusicSeekBar.setProgress(0);
                    //mMusicSeekBar.setMax(0);
                    mMusicSeekBar.setEnabled(false);
                    //
                    // mSongDurationTV.setText(TimeUtils.parseMMSSString(0));
                    //mSongProgressTV.setText(TimeUtils.parseMMSSString(0));
                    ManyActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showUploadDialog();
                        }
                    });
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
        setContentView(R.layout.activity_many);
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

                    @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
                    time = df.format(new Date());
                    mHandler.sendEmptyMessage(MUSIC_INIT);

                    //
                    //mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.aiqingyu);
                    mMediaPlayer = new MediaPlayer();
                    String fileUrl = Consts.SONG_DIR +currentSong.getSname() + "-" + currentSong.getSingerName() + "-" + currentSong.getAlbum() + "-" + currentSong.getSid() + ".mp3";
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



                    //初始化录音
                    String fileName = currentSong.getSname() + "-" + currentSong.getSingerName() + "-" + currentSong.getAlbum() + "-" + currentSong.getSid()+"-"+time+"-0.mp3";
                    initRecorder(Consts.SAVE_SONG_DIR, fileName);

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

                    mMediaPlayer.start();
                    recordStart();
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

        new AsyncTask<String, Integer, String>() {

            @Override
            protected String doInBackground(String... strings) {
                InputStream inputStream = null;
                try {
                    inputStream = new FileInputStream( Consts.SONG_DIR+ currentSong.getSname() + "-" + currentSong.getSingerName() + "-" + currentSong.getAlbum() + "-" + currentSong.getSid() + ".krc");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(500);
                    LyricsReader lyricsReader = new LyricsReader();
                    byte[] data = new byte[inputStream.available()];
                    inputStream.read(data);
                    lyricsReader.loadLrc(data, null, Consts.SONG_DIR + currentSong.getSname() + "-" + currentSong.getSingerName() + "-" + currentSong.getAlbum() + "-" + currentSong.getSid() + ".krc");
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
        recordStop();
        super.onBackPressed();
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

    public void initRecorder(String saveDir, String fileName) {
        //mediaRecorder.reset();
        mMediaRecorder = new MediaRecorder();
        // 储存下载文件的目录
        File filedir = new File(saveDir);
        filedir.mkdir();
        String songPathString = saveDir + fileName;
        File soundFile = new File(songPathString);

        try {
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
            mMediaRecorder.setOutputFile(soundFile.getAbsolutePath());
            // 设置录音的来源（从哪里录音）
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void recordStart() {

        try {
            mMediaRecorder.prepare();


        } catch (IllegalStateException e) {
            System.out.print(e.getMessage());
        } catch (IOException e) {
            System.out.print(e.getMessage());
        }
        mMediaRecorder.start();
    }

    public void recordStop() {
        if (mMediaRecorder != null) {
            try {
                mMediaRecorder.stop();
            } catch (IllegalStateException e) {
                // TODO 如果当前java状态和jni里面的状态不一致，
                //e.printStackTrace();
                mMediaRecorder = null;
                mMediaRecorder = new MediaRecorder();
                mMediaRecorder.stop();
            }
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
    }

    public void showUploadDialog(){

        AlertDialog.Builder builder=new AlertDialog.Builder(ManyActivity.this);
        //对对话框内容进行定义
        builder.setTitle("上传");
        builder.setMessage("是否上传并保存此次歌词记录");

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        File file=new File(Consts.SAVE_SONG_DIR+currentSong.getSname() + "-" + currentSong.getSingerName() + "-" + currentSong.getAlbum() + "-" + currentSong.getSid()+"-"+time+"-0.mp3");
                        file.delete();
                        //System.out.println(Consts.SAVE_SONG_DIR+currentSong.getSname() + "-" + currentSong.getSingerName() + "-" + currentSong.getAlbum() + "-" + currentSong.getSid()+"-"+time+"-0.mp3");
                        finish();
                    }
                }).show();
    }

    @Override
    protected void onDestroy() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        recordStop();
        super.onDestroy();
    }

    public static void actionStart(Context context,Song song){
        Intent intent=new Intent(context,ManyActivity.class);
        intent.putExtra("song_data_from_recordPrepare",song);
        context.startActivity(intent);
    }

}