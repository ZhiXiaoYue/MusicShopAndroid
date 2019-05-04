package com.example.jill.firsttry.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.jill.firsttry.R;
import com.example.jill.firsttry.Utils.Consts;
import com.example.jill.firsttry.Utils.DownloadUtil;
import com.example.jill.firsttry.Utils.ImageUtil;
import com.example.jill.firsttry.model.Song;
import com.example.jill.firsttry.model.UserRecord;
import com.example.jill.firsttry.model.global_val.AppContext;
import com.example.jill.firsttry.model.response.BaseResponse;
import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.provider.Telephony.Mms.Part.TEXT;

/**
 * 进入准备界面：试听或长传或修改
 */
public class SubmitPrepareActivity extends Activity {
    private Button submitButton;
    private Button listenButton;
    private Button modifyButton;
    private TextView singer;
    private TextView songName;
    private ImageUtil songIcon;
    private ProgressDialog progressDialog;
    private Song currentSong;
    private UserRecord currentRecord;

    private Song fakeSong;
    private UserRecord fakeUserRecord;

    public SubmitPrepareActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acticity_prepare_submit);
        initComponent(); //绑定所有的组件
        //currentSong = testWithFakeData(); // 测试的时候装一点假数据，后来删掉就改行+testWithFakeData函数+取消下一行的注释
        //从searchctivity获取用户选择的歌曲
        currentSong = (Song) getIntent().getSerializableExtra("song_data");
        currentRecord=(UserRecord)getIntent().getSerializableExtra("record_data");
        //设置界面信息
        singer.setText(currentSong.getSingerName());
        songName.setText(currentSong.getSname());
        songIcon.setImageURL("http://58.87.73.51:8080/musicshop/" + currentSong.getAlbumPic());
        //试听
        listenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListenOriginalActivity.actionStart(SubmitPrepareActivity.this, currentSong, currentRecord);
            }
        });
        //上传
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("执行了click");
                String fileName = Consts.SAVE_SONG_DIR+currentSong.getSname() + "-" + currentSong.getSingerName() + "-" + currentSong.getAlbum() + "-" + currentSong.getSid()+"-"+currentRecord.getTime()+ "-0.mp3";
                showProgressUploadDialog();
                UploadTask uploadTask=new UploadTask(SubmitPrepareActivity.this,currentSong);
                //要上传的文件,url,sid
                uploadTask.execute(fileName,Consts.addRecordURL,Integer.valueOf(currentSong.getSid()).toString());

            }
        });
        //修改
       // final String name_record= fakeSong.getSname() + "-" + fakeSong.getSingerName() + "-" + fakeSong.getAlbum() + "-" + fakeSong.getSid() + fakeUserRecord.getTime() + "-" + fakeUserRecord.getRid()+".mp3";
        //final String URL_record = Consts.ENDPOINT+fakeUserRecord.getFilepath();
//        modifyButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //下载原声录音
//                DownloadTask task = new DownloadTask(SubmitPrepareActivity.this,
//                        new String[]{Consts.SAVE_SONG_DIR},
//                        new String[]{name_record});
//                task.execute(URL_record);
//                showProgressDialog();
//            }
//        });
    }

    /**
     * 异步上传任务
     */
    @SuppressLint("StaticFieldLeak")
    private class UploadTask extends AsyncTask<String, Integer, String> {

        Song currentSong;
        UserRecord userRecord;
        Activity context;

        public void renameFile(String path,String oldname,String newname){
            if(!oldname.equals(newname)){//新的文件名和以前文件名不同时,才有必要进行重命名
                File oldfile=new File(path+"/"+oldname);
                File newfile=new File(path+"/"+newname);
                if(!oldfile.exists()){
                    return;//重命名文件不存在
                }
                if(newfile.exists())//若在该目录下已经有一个文件和新文件名相同，则不允许重命名
                    System.out.println(newname+"已经存在！");
                else oldfile.renameTo(newfile);
            }else{
                System.out.println("新文件名和旧文件名相同...");
            }
        }


        UploadTask(Activity context,Song song){
            this.context=context;
            currentSong=song;
        }

        @Override
        protected void onPostExecute(String result) {
            //最终结果的显示
           progressDialog.setMessage(result);
           progressDialog.setCancelable(true);

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            //显示进度
            progressDialog.setProgress(values[0]);
        }

        @Override
        protected String doInBackground(String... params) {
            System.out.println("异步任务在执行");
            //这里params[0]和params[1]是execute传入的两个参数
            String filePath = params[0];
            String uploadUrl = params[1];
            int sid=Integer.valueOf(params[2]);
            //下面即手机端上传文件的代码
            String LINE_END = "\r\n";
            String PREFIX="--";
            String BOUNDARY = "----WebKitFormBoundary"+"7MA4YWxkTrZu0gW";
            //边界标识，随机生成
           // String BOUNDARY = PREFIX+UUID.randomUUID().toString();
            try {
                URL url = new URL(uploadUrl);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url
                        .openConnection();
                //允许输入流
                httpURLConnection.setDoInput(true);
                //语序输出流
                httpURLConnection.setDoOutput(true);
                //不允许使用缓存
                httpURLConnection.setUseCaches(false);
                //请求方式
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type",
                        "multipart/form-data; boundary=" + BOUNDARY);
                httpURLConnection.setRequestProperty("token",((AppContext)context.getApplication()).getUser().getData());
                httpURLConnection.setRequestProperty("cache-control","no-cache");
                System.out.println(((AppContext)context.getApplication()).getUser().getData());
               // httpURLConnection.setConnectTimeout(30 * 1000);
                httpURLConnection.connect();

                DataOutputStream dos = new DataOutputStream(httpURLConnection
                        .getOutputStream());
                StringBuffer sb=new StringBuffer();
                //构造body
                //分界+\r\n
                sb.append(PREFIX).append(BOUNDARY).append(LINE_END);
                //参数的键
                sb.append("Content-Disposition: form-data; name=\"sid\"\r\n\r\n");
                //sid
                sb.append(Integer.valueOf(sid));
                sb.append(LINE_END);
                sb.append(PREFIX).append(BOUNDARY).append(LINE_END);
                /**
                 * 文件名
                 */
                sb.append("Content-Disposition:form-data; name=\"file\";filename=\"");
                //文件名
               sb.append(filePath.substring(filePath.lastIndexOf("/")+1)+"\"");
                sb.append(LINE_END);
               sb.append("Content-Type:audio/mpeg");
                sb.append(LINE_END);
                sb.append(LINE_END);
                //写入数据
                dos.write(sb.toString().getBytes("utf-8"));
                dos.flush();
                //获取文件总大小
                FileInputStream fis = new FileInputStream(new File(filePath));
                long total = fis.available();
                byte[] buffer = new byte[1024]; // 8k
                int count = 0;
                int length = 0;
                while ((count = fis.read(buffer)) != -1) {
                    dos.write(buffer, 0, count);
                    System.out.println("byte:"+ Arrays.toString(buffer));
                    //获取进度，调用publishProgress()
                    length += count;
                    publishProgress((int) ((length / (float) total) * 100));
                }
                dos.flush();
                //一定还得加换行
                dos.write(LINE_END.getBytes());
                dos.write(LINE_END.getBytes());
                dos.write((PREFIX+BOUNDARY).getBytes());
                dos.flush();
                //获得返回数据
                InputStream is;
                InputStreamReader isr;
                BufferedReader br ;
                String result="";
                String readline=null;

                if (httpURLConnection.getResponseCode() / 100 == 2) { // 2xx code means success
                    is = httpURLConnection.getInputStream();
                    isr= new InputStreamReader(is, "utf-8");
                    br=new BufferedReader(isr);
                    while ((readline=br.readLine())!=null){
                        result+=readline;
                        System.out.println("成功了:"+result);
                    }
                    Gson gson = new Gson();
                    BaseResponse baseResponse = gson.fromJson(result, BaseResponse.class);
                    String data=baseResponse.getData();
                    data=data.replaceAll("\\\\\"", "\"");
                    data=data.replaceAll("\"\\{","{");
                    System.out.println("转义后："+data);
                    userRecord=gson.fromJson(data,UserRecord.class);
                    //重命名文件
                    String oldName=currentSong.getSname() + "-" + currentSong.getSingerName() + "-" + currentSong.getAlbum() + "-" + currentSong.getSid()+"-"+currentRecord.getTime()+ "-0.mp3";
                    String newName=currentSong.getSname() + "-" + currentSong.getSingerName() + "-" + currentSong.getAlbum() + "-" + currentSong.getSid()+"-"+userRecord.getTime()+ "-"+userRecord.getRid()+".mp3";
                    renameFile(Consts.SAVE_SONG_DIR,oldName,newName);
                } else {
                    is = httpURLConnection.getErrorStream();
                    isr= new InputStreamReader(is, "utf-8");
                    br=new BufferedReader(isr);
                    while ((readline=br.readLine())!=null){
                        result+=readline;
                    }
                    System.out.println("错误了:"+result);
                }
                fis.close();
                dos.close();
                is.close();
                return "上传成功";
            } catch (Exception e) {
                e.printStackTrace();
                return "上传失败";
            }
        }
    }

    /**
     * 上传进度对话框
     */
    private void showProgressUploadDialog() {
        System.out.println("显示对话框");
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("请稍后...");// 设置Title
        progressDialog.setMessage("上传中...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setIndeterminate(false);
        progressDialog.setMax(100);
        progressDialog.setCancelable(false);
        progressDialog.show();
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
    @SuppressLint("StaticFieldLeak")
    private class DownloadTask extends
            AsyncTask<String, Integer, List<Integer>> {

        private Activity context;
        private String[] dirs;
        private String[] names;
        List<Integer> rowItems;
        int taskCount;

        DownloadTask(Activity context, String[] dirs, String[] names) {
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
                progressDialog.setMessage("修改中" + (rowItems.size() + 1)
                        + "/" + taskCount);
            }
        }

        @Override
        protected void onPostExecute(List<Integer> rowItems) {
            progressDialog.setTitle("完成");
            progressDialog.setMessage("修改成功!");
            progressDialog.setCancelable(true);
            progressDialog.setButton(TEXT, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ListenOriginalActivity.actionStart(SubmitPrepareActivity.this, fakeSong, fakeUserRecord);
                }
            });
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
        submitButton = findViewById(R.id.button_submit_in_submit);
        listenButton=findViewById(R.id.button_listen_in_submit);
        modifyButton=findViewById(R.id.button_modify_in_submit);
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
        Intent intent = new Intent(context, SubmitPrepareActivity.class);
        // 在Intent中传递数据
        intent.putExtra("song_data", song);
        intent.putExtra("record_data", userRecord);
        // 启动Intent
        context.startActivity(intent);
    }
}
