package com.example.jill.firsttry.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.jill.firstry.event.OnSearchKeyChangedEvent;
import com.example.jill.firsttry.Adapter.BaseRecyclerViewAdapter;
import com.example.jill.firsttry.Adapter.SongAdapter;
import com.example.jill.firsttry.R;
import com.example.jill.firsttry.Utils.Consts;
import com.example.jill.firsttry.Utils.HttpUtil;
import com.example.jill.firsttry.activity.RecordPrepareActivity;
import com.example.jill.firsttry.model.Song;
import com.example.jill.firsttry.model.global_val.AppContext;
import com.example.jill.firsttry.model.response.BaseResponse;
import com.example.jill.firsttry.model.search.SearchBean;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.content.ContentValues.TAG;


/**
 * 歌曲，搜索结果
 * Created by smile on 02/03/2018.
 */

public class SearchSongResultFragment extends BaseCommonFragment {
    RecyclerView rv;
    private SongAdapter adapter;
    Gson gson;
    AppContext appContext;
    //代表LoginActivity的请求码
    public static final int SEARCH_FRAMENT = 1;
    public static boolean SEARCH_BEFORE_LOGIN = false;


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
        RecordPrepareActivity.actionStart((Context) getActivity(), data);
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

        call_1(content);
//        Song song=testWithFakeData();
//        List<Song> songs=new ArrayList<Song>();
//        songs.add(song);
//        adapter.setData(songs);
    }

    private void call_1(String songname) {
        if (((AppContext) getActivity().getApplication()).getUser() == null) {
            Log.d("现在的token", "token是null");
        } else {
            Log.d("现在的token", ((AppContext) getActivity().getApplication()).getUser().getData());
        }
        HttpUtil.sendOkHttpRequestWithHeader(Consts.ENDPOINT + "api/search?keyword=" + songname + "&type=" + Consts.SEARCH_TYPE_FOR_SNAME, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                //Toast.makeText(MainActivity.this,"failed",Toast.LENGTH_SHORT);
                System.out.println("失败了" + e.toString());
                Log.d(TAG, "失败了 " + e.toString());
                getActivity().runOnUiThread(
                        new Runnable() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), "网络不太好哦", Toast.LENGTH_SHORT);
                            }
                        }
                );
            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                try {
                    String responseString = response.body().string();
                    Log.d("search返回的是", responseString);
                    Log.d("search返回的code是", (new Integer(response.code())).toString());
                    if (response.code() == 200) {
                        if (isLogin(responseString)) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    needLogin();
                                }
                            });
                        } else {
                            if (getBaseResponse(responseString).getData().equals("null")) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getActivity(), "未搜索到相关资源", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                responseString = responseString.replaceAll("\"\\[", "\\[");
                                responseString = responseString.replaceAll("\\]\"", "\\]");
                                responseString = responseString.replaceAll("\\\\\"", "\"");
                                System.out.println(responseString);
                                Log.d(TAG, "获得请求数据" + responseString);
                                call_2(responseString);
                            }
                        }
                    } else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), "网络出现错误了", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(TAG, "请求失败了 " + e.toString());
                }
            }
        }, "token", ((AppContext) getActivity().getApplication()).getUser());

    }

    /**
     * 根据返回结果判定是否需要登录
     *
     * @return
     */
    public boolean isLogin(String responseString) {
        return (getBaseResponse(responseString).getStatusCode() == 203);
    }

    /**
     * 获取到基本返回
     * @param responseString
     * @return
     */
    public BaseResponse getBaseResponse(String responseString) {
        gson = new Gson();
        BaseResponse baseResponse = gson.fromJson(responseString, BaseResponse.class);
        return baseResponse;
    }

    public void needLogin() {
        Log.d(TAG, "请登录IIIII");
        SEARCH_BEFORE_LOGIN = true;
        Toast.makeText(getActivity(), "请先登录", Toast.LENGTH_SHORT).show();
        startActivity(LoginAcitivity.class);
    }


    /**
     * 处理登录后的返回结果
     *
     * @param tring
     */
    private void call_2(final String tring) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                gson = new Gson();
                SearchBean searchBean = gson.fromJson(tring, SearchBean.class);
                adapter.setData(searchBean.getData());
//                }
            }
        });
    }

    private Song testWithFakeData() {
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

        adapter = new SongAdapter(getActivity(), R.layout.item_song_detail, getChildFragmentManager());
        adapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerViewAdapter.ViewHolder holder, int position) {
                show(position);
            }
        });

        rv.setAdapter(adapter);
        gson = new Gson();

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
        SEARCH_BEFORE_LOGIN = false;
        super.onDestroy();
    }
}
