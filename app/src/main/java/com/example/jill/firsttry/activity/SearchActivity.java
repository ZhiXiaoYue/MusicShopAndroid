package com.example.jill.firsttry.activity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.jill.firsttry.Adapter.SearchAdapter;
import com.example.jill.firsttry.R;
import com.example.jill.firsttry.Utils.DownloadUtil;
import com.example.jill.firsttry.Utils.HttpUtil;
import com.example.jill.firsttry.model.Song;
import com.example.jill.firsttry.model.download.DownloadBean;
import com.example.jill.firsttry.model.search.SearchBean;
import com.example.jill.firsttry.model.global_val.AppContext;
import com.google.gson.Gson;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SearchActivity extends AppCompatActivity {

    private SearchAdapter searchAdapter = null;
    private ListView searchListView;
    private TextView textView1;
    private EditText searchEditText;
    private Gson gson;
    private SearchBean searchBean;
    private SearchView searchView;

    //public static final String EXTRA_MESSAGE = "com.example.a46639.testdownload.MESSAGE";


    //public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置状态栏可见；
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        setContentView(R.layout.activity_search);

        searchListView = findViewById(R.id.list_view_search);
        searchView=findViewById(R.id.searchview);
        textView1=findViewById(R.id.textview1);

        // Button button=findViewById(R.id.button1);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//            }
//        });




        //去掉搜索框下划线
        if (searchView != null) {
            int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
            TextView textView = (TextView) searchView.findViewById(id);
            textView.setTextColor(Color.GRAY);//字体颜色
            textView.setHintTextColor(Color.GRAY);//提示字体颜色

            try {        //--拿到字节码
                Class<?> argClass = searchView.getClass();
                //--指定某个私有属性,mSearchPlate是搜索框父布局的名字
                Field ownField = argClass.getDeclaredField("mSearchPlate");
                //--暴力反射,只有暴力反射才能拿到私有属性
                ownField.setAccessible(true);
                View mView = (View) ownField.get(searchView);
                //--设置背景
//                mView.setBackgroundResource(R.xml.searchview_line);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Toast.makeText(MainActivity.this, "您输入的文本为" + query, Toast.LENGTH_SHORT).show();
                call_1(query.trim());

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

//        Button searchButton = findViewById(R.id.search_button);
//        searchEditText = findViewById(R.id.search_song_edittext);
//        textView1 = findViewById(R.id.textview1);
        //Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        // setSupportActionBar(toolbar);
        //搜索按钮设置监听
//        searchButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                call_1(searchEditText.getText().toString().trim());
//            }
//        });

    }

    //隐藏键盘
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideKeyboard(v, ev)) {
                hideKeyboard(v.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }
    private boolean isShouldHideKeyboard(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0],
                    top = l[1],
                    bottom = top + v.getHeight(),
                    right = left + v.getWidth();
            return !(event.getX() > left) || !(event.getX() < right)
                    || !(event.getY() > top) || !(event.getY() < bottom);
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditText上，和用户用轨迹球选择其他的焦点
        return false;
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void hideKeyboard(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            Objects.requireNonNull(im).hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    //发送请求，并将接受的JSON加表头
    private void call_1(String songname) {
        HttpUtil.sendOkHttpRequestWithHeader("http://mrquin.space/musicshop/api/search?keyword="+songname+"&type=0", new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                //Toast.makeText(MainActivity.this,"failed",Toast.LENGTH_SHORT);
                runOnUiThread(
                        new Runnable() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void run() {
                                textView1.setText("failed");
                            }
                        }
                );
            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response)  {
                if(response.code()==200){
                    try {
                        String responseString = response.body().string();
                        responseString=responseString.replaceAll("\"\\[","\\[");
                        responseString=responseString.replaceAll("\\]\"","\\]");
                        responseString=responseString.replaceAll("\\\\\"","\"");
                        System.out.println(responseString);
                        call_2(responseString);
                    }catch (IOException e){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //textView1.setText("failed");
                                textView1.setText("出现了不可描述的错误。。。");
                            }
                        });
                    }
                }
                else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //textView1.setText("failed");
                            textView1.setText("没能找到相关资源哦。。。");
                        }
                    });
                }
            }
        },"token","13051393220^1537679154");


    }

    //使用listview显示搜索信息
    private void call_2(final String tring) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                gson = new Gson();
                searchBean = gson.fromJson(tring, SearchBean.class);
                final AppContext app = (AppContext)SearchActivity.this.getApplication();
                searchAdapter = new SearchAdapter(SearchActivity.this, searchBean.data,(AppContext)getApplicationContext());
                searchListView.setAdapter(searchAdapter);
                searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Song key = searchBean.data.get(position);
//                        textView1.setText(key.getSid());
                        //保存点击歌曲的相关信息
                        final AppContext app = (AppContext )getApplication();
                        app.setSong(key);
                        playSong();
                    }
                });
            }
        });
    }

    private void playSong(){
        final AppContext app = (AppContext )getApplication();
        String url="http://mrquin.space/musicshop/api/download?sid="+ app.getSong().getSid();
        HttpUtil.sendOkHttpRequestWithHeader(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseString = response.body().string();
                responseString=responseString.replaceAll("\"\\{","\\{");
                responseString=responseString.replaceAll("\\}\"","\\}");
                responseString=responseString.replaceAll("\\\\\"","\"");
                Gson gson = new Gson();
                DownloadBean downloadBean = gson.fromJson(responseString, DownloadBean.class);
                //下载歌词
                app.setDownloadBean(downloadBean);
                String lyricName=downloadBean.getData().getSname() + "-" + downloadBean.getData().getSingerName() + "-" +downloadBean.getData().getAlbum()+"-"+downloadBean.getData().getSid()+".krc";
               DownloadUtil downloadUtil=new DownloadUtil(SearchActivity.this);
                downloadUtil.download("http://58.87.73.51/musicshop/"+downloadBean.getData().getLyric(),"/mnt/sdcard/MusicShopDownLoad/Songs//",lyricName);

                Intent intent = new Intent(SearchActivity.this,
                        NewPlayActivity.class);
                startActivity(intent);
            }
        },"token","13051393220^1537679154");


    }
}



