package com.example.jill.firsttry.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jill.firstry.event.OnSearchByIdFinishEvent;
import com.example.jill.firsttry.Adapter.SongCardAdapter;
import com.example.jill.firsttry.R;
import com.example.jill.firsttry.Utils.Consts;
import com.example.jill.firsttry.model.QueryRecordBean;
import com.example.jill.firsttry.model.Song;
import com.example.jill.firsttry.model.UserRecord;
import com.example.jill.firsttry.model.UserRecordSimple;
import com.example.jill.firsttry.model.global_val.AppContext;
import com.example.jill.firsttry.model.global_val.UserBean;
import com.example.jill.firsttry.model.response.BaseResponse;
import com.example.jill.firsttry.model.search.AllRecordBean;
import com.google.gson.Gson;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int GET_DATA_SUCCESS = 1;
    public static final int NETWORK_ERROR = 2;
    public static final int SERVER_ERROR = 3;
    //代表LoginActivity的请求码
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private List<Song> songList = new ArrayList<>();
    final OkHttpClient client = new OkHttpClient();
    private AppContext app ;
    private List<UserRecord> recordList = new ArrayList<>();
    private int flag;
    ImageView recommandImage;
    ImageView userRecordImage;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case NETWORK_ERROR:
                    Toast.makeText(MainActivity.this, "请检查网络连接", Toast.LENGTH_LONG).show();
                    break;
                case SERVER_ERROR:
                    Toast.makeText(MainActivity.this, "服务器发生错误，请联系客服", Toast.LENGTH_LONG).show();
                    break;
            }
            if(msg.what== GET_DATA_SUCCESS){
                String ReturnMessage = (String) msg.obj;
                Log.i("获取的返回信息",ReturnMessage);
                final QueryRecordBean queryRecordBean = new Gson().fromJson(ReturnMessage, QueryRecordBean.class);
                String returnData = queryRecordBean.getData();
                switch (returnData){
                    case "none":
//                        songs = new Song[]{testWithFakeData(),testWithFakeData(),testWithFakeData(),
//                                testWithFakeData(),testWithFakeData(),testWithFakeData(),testWithFakeData()}; // 推荐数据
                        userRecordImage.setVisibility(View.INVISIBLE); //显示推荐图片
                        recommandImage.setVisibility(View.VISIBLE);
                        break;
                    default:
                        try {
                            JSONObject jsonObject = new JSONObject(returnData);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        }
    };
    ImageButton menu;

    public MainActivity() {
    }

    @SuppressLint("ResourceAsColor")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置状态栏可见；
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        setContentView(R.layout.activity_main);
        initWindow();
        app = (AppContext)getApplication();
        ImageView button_pic= findViewById(R.id.imageMenu);
        button_pic.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(app.getState() == null){
                Intent intent = new Intent(MainActivity.this,
                        LoginAcitivity.class);
                startActivity(intent);}
                else{
                    Intent intent = new  Intent(MainActivity.this,
                            User_infoActivity.class);
                    startActivity(intent);
                }
            }
        });
        drawerLayout = findViewById(R.id.activity_na);
        navigationView = findViewById(R.id.nav);
        menu= findViewById(R.id.main_menu);
        menu.setOnClickListener(this);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawerLayout.closeDrawer(navigationView);

                final AppContext app = (AppContext)getApplication();
                switch (item.getItemId()) {
                    case R.id.favorite:
                        if(app.getState()!=null) {
                            Intent intent1 = new Intent(MainActivity.this,
                                    User_infoActivity.class);
                            startActivity(intent1);
                        }
                        else {
                            Intent intent1 = new Intent(MainActivity.this,
                                    LoginAcitivity.class);
                            startActivity(intent1);
                        }
                        break;
                    case R.id.wallet:
                        Intent intent2 = new Intent(MainActivity.this,
                                MyModifyActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.photo:
                        Intent intent3 = new Intent(MainActivity.this,
                                MysongListActivity.class);
                        startActivity(intent3);
                        break;
                    case R.id.company:
                        Intent intent4 = new Intent(MainActivity.this,CompanyListActivity.class);
                        startActivity(intent4);
                        break;
                    case R.id.dress:
                        if (app.getUser()!=null) {

                            new  AlertDialog.Builder(MainActivity.this)
                                    .setTitle("确认" )
                                    .setMessage("确定注销？" )
                                    .setNegativeButton("是" ,   new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            app.setState(null);
                                            app.setUser(null);
                                            refresh();
                                            Toast.makeText(MainActivity.this, "注销成功", Toast.LENGTH_SHORT).show();
                                            drawerLayout.closeDrawers();
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
                        else Toast.makeText(MainActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
    }
    private void refresh() {
        finish();
        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        startActivity(intent);
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        flag = 0; //判断是推荐还是用户记录
        recommandImage = findViewById(R.id.recommandToYou);
        userRecordImage = findViewById(R.id.user_record_in_main);
        UserBean user= app.getUser();
        TextView u_name = findViewById(R.id.act_m_user_name);
        if(app.getState() == null) { //如果用户没有登录
//            songs = new Song[]{testWithFakeData(),testWithFakeData(),testWithFakeData(),
//                    testWithFakeData(),testWithFakeData(),testWithFakeData(),testWithFakeData()}; // 推荐数据
            userRecordImage.setVisibility(View.INVISIBLE); //显示推荐图片
            recommandImage.setVisibility(View.VISIBLE);
            flag = 0;
            u_name.setText("请先登录");
            u_name.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this,
                            LoginAcitivity.class);
                    startActivity(intent);
                }
            });
        }
        else {  //如果用户登录了
            //获取用户的上传记录
            flag = 1;
            @SuppressLint("HandlerLeak") final Handler mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                switch (msg.what){
                    case NETWORK_ERROR:
                        Toast.makeText(MainActivity.this, "验证码发送失败，请检查网络连接", Toast.LENGTH_LONG).show();
                        break;
                    case SERVER_ERROR:
                        Toast.makeText(MainActivity.this, "服务器发生错误，请联系客服", Toast.LENGTH_LONG).show();
                        break;
                }
                if(msg.what==1){
                    String returnMessage = (String) msg.obj;
                    returnMessage= returnMessage.replaceAll("\\\\\"", "\"");
                    returnMessage= returnMessage.replaceAll("\"\\[", "\\[");
                    returnMessage= returnMessage.replaceAll("\\]\"", "\\]");
                    AllRecordBean arb = new Gson().fromJson(returnMessage, AllRecordBean.class);

                    String A = arb.getStatusExpression();
                    ArrayList<UserRecordSimple> userRecordSimples = arb.getData();
                    for (UserRecordSimple userRecordSimple : userRecordSimples) {
                        String rid = String.valueOf(userRecordSimple.getRid());
                        //根据rid查找用户记录
                        @SuppressLint("HandlerLeak") final Handler mHandler2 = new Handler(){
                            @Override
                            public void handleMessage(Message msg){
                            switch (msg.what){
                            case NETWORK_ERROR:
                                Toast.makeText(MainActivity.this, "验证码发送失败，请检查网络连接", Toast.LENGTH_LONG).show();
                            break;
                            case SERVER_ERROR:
                                Toast.makeText(MainActivity.this, "服务器发生错误，请联系客服", Toast.LENGTH_LONG).show();
                            break;
                            }
                            if(msg.what==1){
                                String returnMessage = (String) msg.obj;
                                BaseResponse br =  new Gson().fromJson(returnMessage, BaseResponse.class);
                                String A = br.getStatusExpression();
                                if(A.equals("Success")){
                                    returnMessage = br.getData();
                                    returnMessage= returnMessage.replaceAll("\\\\\"", "\"");
                                    UserRecord ur =  new Gson().fromJson(returnMessage, UserRecord.class);
                                    recordList.add(ur);
                                    songList.add(ur.getMusic());
                                    System.out.println("测试"+ur.getMusic().getSingerName());
                                }
                            else{
                                    Toast.makeText(MainActivity.this, "服务器发生错误，请联系客服", Toast.LENGTH_LONG).show();
                                }
                            }
                            }
                        };
                        final OkHttpClient client = new OkHttpClient();
                        String mUrl = String.format("http://58.87.73.51:8080/musicshop/api/findbyrid?rid=%s", rid);
                        final Request request = new Request.Builder()
                            .addHeader("token", getUserToken(MainActivity.this))
                            .url(mUrl)
                            .build();
                        new Thread(new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                            @Override
                            public void run() {
                                Response response;
                                try {
                                //回调
                                    response = client.newCall(request).execute();
                                    if (response.isSuccessful()) {
                                    //将服务器响应的参数response.body().string())发送到hanlder中，并更新ui
                                        mHandler2.obtainMessage(1, Objects.requireNonNull(response.body()).string()).sendToTarget();
                                    } else {
                                        throw new IOException("Unexpected code:" + response);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                    songList.clear();
                    if(!A.equals("Success")){
                        Toast.makeText(MainActivity.this, "服务器发生错误，请联系客服", Toast.LENGTH_LONG).show();
                    }
                }
            }
        };
        final OkHttpClient client = new OkHttpClient();
        String mUrl = "http://58.87.73.51:8080/musicshop/api/queryrecord";
        final Request request = new Request.Builder()
                .addHeader("token", getUserToken(MainActivity.this))
                .url(mUrl)
                .build();
        Thread thread = new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                Response response;
                try {
                    //回调
                    response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        //将服务器响应的参数response.body().string())发送到hanlder中，并更新ui
                        mHandler.obtainMessage(1, Objects.requireNonNull(response.body()).string()).sendToTarget();
                    } else {
                        throw new IOException("Unexpected code:" + response);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        recommandImage.setVisibility(View.INVISIBLE);
        userRecordImage.setVisibility(View.VISIBLE); //显示记录
            u_name.setText(user.getName());
        }

        ImageButton buttonS = findViewById(R.id.button_search);
        buttonS.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,
                        SearchActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        /* 3.15
         * 填入歌曲
         */
        GridLayoutManager layoutManager = new GridLayoutManager(this,3);
        RecyclerView recyclerView = findViewById(R.id.recycler_view_in_main);
        recyclerView.setLayoutManager(layoutManager);
        final SongCardAdapter mAdapter = new SongCardAdapter(songList);
        mAdapter.setOnItemClickListener(new SongCardAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Song currentSong = songList.get(position); //选中的歌曲
                if(flag == 1) {
                    UserRecord currentRecord = recordList.get(position);
                    if (hasDownLoad(currentRecord, currentSong)) {
                        ListenOriginalActivity.actionStart(MainActivity.this, currentSong, currentRecord);
                    } else {
                        DownloadPrepareActivity.actionStart(MainActivity.this, currentSong, currentRecord);
                    }
                }
                else{
                    // 推荐给用户之后的跳转界面
                    RecordPrepareActivity.actionStart(MainActivity.this,currentSong);
                }
            }
        });
        recyclerView.setAdapter(mAdapter);
    }

    /**
     * 装一点假的数据，后来删掉就好
     */
    private Song testWithFakeData(){
        Song song = new Song();
        song.setSname("forgive");
        song.setSingerName("Liu");
        song.setSid(19);
        song.setAlbum("firstfans");
        song.setAlbumPic("static/album_thumbnails/Liu-firstfans.jpg");
        song.setFilePath("static/music/forgive-Liu.mp3");
        song.setInstrumental("static/instru/forgiveLiu.mp3");
        song.setLyric("static/lyric/forgiveLiu.krc");
        return song;
    }


    public void postRequest(String phone) {
        int Uuid = (int) ((Math.random() * 9 + 1) * 100000);
        String UidString = Uuid + "";
        @SuppressLint("DefaultLocale")
        String mUrl = String.format("http://58.87.73.51:8080/musicshop/api/login?mobile=%s&uuid=%s/", phone, UidString);
        final Request request = new Request.Builder()
                .url(mUrl)
                .build();
        //新建一个线程，用于得到服务器响应的参数
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                Response response;
                try {
                    //回调
                    response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        //将服务器响应的参数response.body().string())发送到hanlder中，并更新ui
                        mHandler.obtainMessage(1, Objects.requireNonNull(response.body()).string()).sendToTarget();
                    } else {
                        throw new IOException("Unexpected code:" + response);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    /**
     * 初始化songList,将数组中的数据填入
     */
    private void initWindow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            int statusColor = Color.parseColor("#1976d2");
            tintManager.setStatusBarTintColor(statusColor);
            tintManager.setStatusBarTintEnabled(true);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.main_menu://点击菜单，跳出侧滑菜单
                if (drawerLayout.isDrawerOpen(navigationView)){
                    drawerLayout.closeDrawer(navigationView);
                }else {
                    drawerLayout.openDrawer(navigationView);
                }
                break;
        }


    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            exitBy2Click();      //调用双击退出函数
        }
        return false;
    }
    /**
     * 双击退出函数
     */
    private static Boolean isExit = false;
    private void exitBy2Click() {
        Timer tExit;
        if (!isExit) {
            isExit = true; // 准备退出
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // 取消退出
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

        } else {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            System.exit(0);
        }
    }

    /**
     * 判断本地是否存在
     */
    private boolean hasDownLoad(UserRecord record,Song song){
        return new File(Consts.SONG_DIR+song.getSname() + "-" + song.getSingerName() + "-" + song.getAlbum() + "-" + song.getSid() +"-"+record.getTime()+"-"+record.getRid()+".mp3").exists();
    }
    private String getUserToken(Activity activity){
        AppContext app = (AppContext)activity.getApplication();
        UserBean currentUser= app.getUser();
        if(currentUser == null)
            needLogin(activity);
        return app.getUser().getData();
    }

    private void needLogin(Activity activity) {
        Log.d(".", "请登录");
        Toast.makeText(activity, "请先登录", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(activity, LoginAcitivity.class);
        activity.startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnSearchByIdFinishEvent(OnSearchByIdFinishEvent event) {
        Song song=event.getSong();
        System.out.println("传出了"+song.getSname());
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}
