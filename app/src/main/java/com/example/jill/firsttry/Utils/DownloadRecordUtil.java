package com.example.jill.firsttry.Utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.example.jill.firsttry.activity.ListenOriginalActivity;
import com.example.jill.firsttry.activity.ManyActivity;
import com.example.jill.firsttry.model.Song;
import com.example.jill.firsttry.model.UserRecord;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.support.constraint.Constraints.TAG;

public class DownloadRecordUtil {

    //线程的标识符
    private boolean flag=true;
    //进度条最大值
    private static final int PROGRESS_MAX=100;
    private Context activityContext;
    private ProgressDialog dialog;
    private static final int UPDATE_PROGRESS = 1;
    private Song currentSong;
    private UserRecord currentRecord;


    public DownloadRecordUtil(Context activityContext, Song song,UserRecord userRecord) {
        this.activityContext=activityContext;
        currentSong=song;
        currentRecord=userRecord;
    }

    private void showProgressDialog(){
        //创建进度条对话框对象
        dialog = new ProgressDialog(activityContext);
        // 设置进度条的样式
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 设置样式
        //对话框 不终止对话框(如果它为true 代表进度条的值是0)
        dialog.setIndeterminate(false);
        //失去焦点的时候，不消失对话框
        dialog.setCancelable(false);
        // 设置消息
        dialog.setMessage("正在下载伴奏");
        // 设置标题
        dialog.setTitle("请稍等");
        // 进度条总大小
        dialog.setMax(PROGRESS_MAX);
        // 显示出来
        dialog.show();
    }

    /**
     * @param url 下载连接
     * @param saveDir 储存下载文件的SDCard目录
     * @param fileName 文件名
     */
    public void download(final String url, final String saveDir, final String fileName,final int type) throws IOException{
        OkHttpClient client = new OkHttpClient.Builder().build();
        Request request = new Request.Builder().url(url)
                .get().build();
        Call call = client.newCall(request);
            System.out.println("发送请求了");
            System.out.println("请求网址"+url);
            InputStream is = null;
            byte[] buf = new byte[2048];
            int len;
            FileOutputStream fos = null;
            // 储存下载文件的目录
            File filedir = new File(saveDir+"/");
            filedir.mkdir();
            String songPathString = saveDir + fileName;
            System.out.println(songPathString);
            File file = new File(songPathString);
            fos = new FileOutputStream(file);

            //显示进度对话框
            showProgressDialog();

            try{
            Response response = call.execute();
            is = response.body().byteStream();
            long total = response.body().contentLength();
            long sum = 0;
            while ((len = is.read(buf)) != -1) {
                fos.write(buf, 0, len);
                sum += len;
                final int progress = (int) (sum * 1.0f / total * 100);

                // 下载中
                System.out.println("写入中："+progress);
                dialog.setProgress(progress);
            }
            fos.flush();
            dialog.dismiss();
            // 下载完成
            onDownloadSuccess(type);
        } catch (Exception e) {
            Log.d(TAG, "download failed(下载错误）");
            e.printStackTrace();
            dialog.dismiss();
        } finally {
            try {
                if (is != null)
                    is.close();
            } catch (IOException ignored) {
            }
            try {
                if (fos != null)
                    fos.close();
            } catch (IOException ignored) {
            }
        }
    }

    /**
     * @param saveDir fg
     * @return savePath
     * @throws IOException
     * 判断下载目录是否存在
     */
    private String isExistDir(String saveDir) throws IOException {
        // 下载位置
        File downloadFile = new File(Environment.getExternalStorageDirectory(), saveDir);
        if (!downloadFile.mkdirs()) {
            downloadFile.createNewFile();
        }
        return downloadFile.getAbsolutePath();
    }

    private void onDownloadSuccess(int type) {
        //等到歌词下载完成才能进入播放页面
        if(type==Consts.ORIGINAL_RECORD){
            ListenOriginalActivity.actionStart(activityContext,currentSong,currentRecord);
        }
    }

    private void onDownloadFailed()
    {
        Log.d(TAG, "download failed");
    }
}