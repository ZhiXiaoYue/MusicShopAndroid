package com.example.jill.firsttry.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.jill.firstry.event.OnSearchKeyChangedEvent;
import com.example.jill.firsttry.Adapter.BaseRecyclerViewAdapter;
import com.example.jill.firsttry.Adapter.SongAdapter;
import com.example.jill.firsttry.R;
import com.example.jill.firsttry.Utils.Consts;
import com.example.jill.firsttry.Utils.HttpUtil;
import com.example.jill.firsttry.Utils.callListenner;
import com.example.jill.firsttry.activity.LoginAcitivity;
import com.example.jill.firsttry.activity.RecordPrepareActivity;
import com.example.jill.firsttry.activity.SearchActivity;
import com.example.jill.firsttry.model.Song;
import com.example.jill.firsttry.model.global_val.AppContext;
import com.example.jill.firsttry.model.response.BaseResponse;
import com.example.jill.firsttry.model.search.SearchBean;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Address;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 歌曲，搜索结果
 * Created by smile on 02/03/2018.
 */

public class SearchSongResultFragment extends BaseCommonFragment {
    RecyclerView rv;
    private SongAdapter adapter;
    Gson gson;

    public static SearchSongResultFragment newInstance() {

        Bundle args = new Bundle();
        SearchSongResultFragment fragment = new SearchSongResultFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initViews() {
        super.initViews();
        EventBus.getDefault().register(this);

        rv = findViewById(R.id.rv);
        rv.setHasFixedSize(true);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(layoutManager);

        DividerItemDecoration decoration = new DividerItemDecoration(getActivity(), RecyclerView.VERTICAL);
        rv.addItemDecoration(decoration);

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnSearchKeyChangedEvent(OnSearchKeyChangedEvent event) {
        fetchData(event.getContent());
    }

    private void show(int position) {
        Song data = adapter.getData(position);
        adapter.notifyDataSetChanged();
        Intent intent = new Intent();
        intent.setClass(getActivity(), RecordPrepareActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("Song", data);
        intent.putExtras(bundle);
        this.startActivity(intent);
    }


    private void fetchData(String content) {
//        Api.getInstance().searchSong(content).subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new HttpListener<ListResponse<Song>>(getMainActivity()) {
//                    @Override
//                    public void onSucceeded(final ListResponse<Song> data) {
//                        super.onSucceeded(data);
//                        adapter.setData(DataUtil.fill(data.getData()));
//                    }
//                });
//        HttpUtil.getInstance().searchSong(content, Consts.SEARCH_TYPE_FOR_SNAME, sp.getToken(), new callListenner(getMainActivity()) {
//            @Override
//            public void onSucceeded(Call call, BaseResponse baseResponse) throws IOException {
//                System.out.println("夜夜夜夜");
//               // List<Song> Songs= new Gson().fromJson(baseResponse.data,(List<Song>).class);
//            }
//        });

       // call_1(content);
        Song song=testWithFakeData();
        List<Song> songs=new ArrayList<Song>();
        songs.add(song);
        adapter.setData(songs);
    }

    private void call_1(String songname) {
        HttpUtil.sendOkHttpRequestWithHeader(Consts.ENDPOINT+"api/search?keyword"+songname+"&type="+Consts.SEARCH_SONG, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                //Toast.makeText(MainActivity.this,"failed",Toast.LENGTH_SHORT);
                getActivity().runOnUiThread(
                        new Runnable() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(),"网络不太好哦",Toast.LENGTH_SHORT);
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
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(),"出现了不可描述的错误。。。failed",Toast.LENGTH_SHORT);
                            }
                        });
                    }
                }
                else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //textView1.setText("failed");
                            Toast.makeText(getActivity(),"没能找到相关资源",Toast.LENGTH_SHORT);
                        }
                    });
                }
            }
        },"token","13051393220^1537679154");


    }

    //使用listview显示搜索信息
    private void call_2(final String tring) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                gson = new Gson();
                SearchBean searchBean = gson.fromJson(tring, SearchBean.class);
                adapter.setData(searchBean.getData());
                if(searchBean.getStatusCode().equals("203")){
                    Toast.makeText(getActivity(),"请先登录",Toast.LENGTH_SHORT);
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), LoginAcitivity.class);
                    getActivity().startActivity(intent);
                }
            }
        });
    }

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

    @Override
    protected void initDatas() {
        super.initDatas();

        adapter = new SongAdapter(getActivity(), R.layout.item_song_detail,getChildFragmentManager());
        adapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerViewAdapter.ViewHolder holder, int position) {
                show(position);
            }
        });

        rv.setAdapter(adapter);
        gson=new Gson();
    }

    @Override
    protected void initListener() {
        super.initListener();
    }


    @Override
    protected View getLayoutView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_select_list, null);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
