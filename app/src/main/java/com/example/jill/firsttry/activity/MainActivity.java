package com.example.jill.firsttry.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.icu.text.AlphabeticIndex;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jill.firsttry.Adapter.SongCardAdapter;
import com.example.jill.firsttry.R;
import com.example.jill.firsttry.model.Song;
import com.example.jill.firsttry.model.global_val.AppContext;
import com.example.jill.firsttry.model.global_val.UserBean;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Song[] songs =  {testWithFakeData(), testWithFakeData(),testWithFakeData(),testWithFakeData()};//假数据
    private List<Song> songList = new ArrayList<>();
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

        final AppContext app = (AppContext)getApplication();
         UserBean user= app.getUser();
        TextView u_name = findViewById(R.id.act_m_user_name);
        if(user != null) u_name.setText(user.getName());
        else {
            u_name.setText("请先登录");
            u_name.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this,
                            LoginAcitivity.class);
                    startActivity(intent);
                }
            });
        }
        ImageButton buttonS = findViewById(R.id.button_search);
        buttonS.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,
                        SearchActivity.class);
                startActivity(intent);
            }
        });
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
                                SetActivity.class);
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
        /* 3.15
         * 填入歌曲
         */
        initSongs();
        RecyclerView recyclerView = findViewById(R.id.recycler_view_in_main);
        GridLayoutManager layoutManager = new GridLayoutManager(this,3);
        recyclerView.setLayoutManager(layoutManager);
        final SongCardAdapter mAdapter = new SongCardAdapter(songList);
        mAdapter.setOnItemClickListener(new SongCardAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                RecordPrepareActivity.actionStart(MainActivity.this,songList.get(position));
            }
        });
        recyclerView.setAdapter(mAdapter);
    }
    private void refresh() {
        finish();
        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        startActivity(intent);
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
     * 初始化songList,将数组中的数据填入
     */
    private void initSongs(){
        songList.clear();
        songList.addAll(Arrays.asList(songs));
    }
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
}
