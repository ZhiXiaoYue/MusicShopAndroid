package com.example.jill.firsttry.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.jill.firsttry.R;
import com.example.jill.firsttry.Utils.Consts;
import com.example.jill.firsttry.Utils.ImageUtil;
import com.example.jill.firsttry.model.Song;
import com.example.jill.firsttry.model.UserRecord;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 进入准备界面：下载
 */
public class DownloadPrepareActivity extends Activity {
   // public static String URL="http://np01.sycdn.kuwo.cn/0bdda78b64f000dda8ee7dc5526244af/5cc91796/resource/n3/15/95/3958571150.mp3";
    private Button downloadButton;
    private TextView singer;
    private TextView songName;
    private ImageUtil songIcon;
    private Song currentSong;
    private UserRecord userRecord;
    private ProgressDialog progressDialog;

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
                String name_lyric=currentSong.getSname() + "-" + currentSong.getSingerName() + "-" + currentSong.getAlbum() + "-" + currentSong.getSid() + ".krc";
                String name_accompany=currentSong.getSname() + "-" + currentSong.getSingerName() + "-" + currentSong.getAlbum() + "-" + currentSong.getSid() + ".mp3";
                String name_record= currentSong.getSname() + "-" + currentSong.getSingerName() + "-" + currentSong.getAlbum() + "-" + currentSong.getSid() + userRecord.getTime() + "-" + userRecord.getRid()+".mp3";
                String URL_lyric = Consts.ENDPOINT+currentSong.getLyric();
                String URL_accompany = Consts.ENDPOINT+currentSong.getInstrumental();
                String URL_record = Consts.ENDPOINT+userRecord.getFilepath();
                // TODO: 2019/4/30 下载歌曲，下载完成后跳转播放,url为recordUrl
                //如果伴奏存在就不去下载了
                if (!(new File(Consts.SONG_DIR + currentSong.getSname() + "-" + currentSong.getSingerName() + "-" + currentSong.getAlbum() + "-" + currentSong.getSid() + ".mp3").exists())) {
                    //下载原声录音
                    DownloadTask task = new DownloadTask(DownloadPrepareActivity.this,
                            new String[]{Consts.SAVE_SONG_DIR},
                            new String[]{name_record});
                    task.execute(URL_record);
                }else {
                    //下载原声录音和伴奏
                    DownloadTask task = new DownloadTask(DownloadPrepareActivity.this,
                            new String[]{Consts.SONG_DIR, Consts.SONG_DIR, Consts.SAVE_SONG_DIR},
                            new String[]{name_lyric,name_accompany,name_record});
                    task.execute(URL_lyric,URL_accompany,URL_record);
                }
                showProgressDialog();
            }
        });
    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("请稍后");// 设置Title
        progressDialog.setMessage("下载中");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setIndeterminate(false);
        progressDialog.setMax(100);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    /**
     * 异步下载任务
     */
    private class DownloadTask extends
            AsyncTask<String, Integer, List<Integer>> {

        private Activity context;
        private String[] dirs;
        private String[] names;
        List<Integer> rowItems;
        int taskCount;

        public DownloadTask(Activity context, String[] dirs, String[] names) {
            this.context = context;
            this.dirs = dirs;
            this.names = names;
        }

        @Override
        protected List<Integer> doInBackground(String... urls) {
            taskCount = urls.length;
            rowItems = new ArrayList<Integer>();
            int tmp=0;
            for (String url : urls) {
                download(url,dirs[tmp],names[tmp]);
                rowItems.add(1);
                tmp++;
            }
            return rowItems;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            progressDialog.setProgress(progress[0]);
            if (rowItems != null) {
                progressDialog.setMessage("下载中" + (rowItems.size() + 1)
                        + "/" + taskCount);
            }
        }

        @Override
        protected void onPostExecute(List<Integer> rowItems) {
            progressDialog.dismiss();
            ListenOriginalActivity.actionStart(DownloadPrepareActivity.this, currentSong, userRecord);
        }

        /**
         * 下载Image
         *
         * @param urlString
         * @return
         */
        private void download(String urlString, String dirname, String filename) {
            int count = 0;

            URL url;
            OutputStream output = null;

            try {
                url = new URL(urlString);
                //创建一个HttpURLConnection连接
                OkHttpClient client = new OkHttpClient();
                //创建一个Request
                Request request = new Request.Builder()
                        .get()
                        .url(url)
                        .build();
                //通过client发起请求
                Call call=client.newCall(request);
                Response response=call.execute();

                InputStream input;
                if (response.code() / 100 == 2) { // 2xx code means success
                    long lengthOfFile = response.body().contentLength();
                    input = response.body().byteStream();
                    //文件夹
                    File dir = new File(dirname);
                    if (!dir.exists()) {
                        dir.mkdir();
                    }
                    //本地文件
                    File file = new File(dirname + filename);
                    if (!file.exists()) {
                        file.createNewFile();
                        //写入本地
                        output = new FileOutputStream(file);
                        byte buffer[] = new byte[4096];
                        int inputSize = -1;
                        long total = 0L;
                        while ((inputSize = input.read(buffer)) != -1) {
                            total += inputSize;
                            publishProgress((int) ((total * 100) / lengthOfFile));
                            output.write(buffer, 0, inputSize);
                        }
                        output.flush();
                    }
                } else {
                    System.out.println("错误码"+response.code());
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    output.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

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
