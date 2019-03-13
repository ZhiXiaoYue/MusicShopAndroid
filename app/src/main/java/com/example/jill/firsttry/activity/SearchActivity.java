package com.example.jill.firsttry.activity;

import android.app.SearchManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.example.jill.firstry.event.OnSearchKeyChangedEvent;
import com.example.jill.firsttry.Adapter.SearchResultAdapter;
import com.example.jill.firsttry.R;
import com.example.jill.firsttry.Utils.KeyboardUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;


public class SearchActivity extends BaseTitleActivity {

    private static final String TAG = "TAG";
    private LayoutInflater inflater;
    private SearchResultAdapter searchResultAdapter;
    private ViewPager vp;
    private LinearLayout ll_search_result_container;
    private TabLayout tabs;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
    }

    @Override
    protected void initViews() {
        super.initViews();
        enableBackMenu();
        inflater = LayoutInflater.from(getActivity());

        tabs = findViewById(R.id.tabs);

        vp = findViewById(R.id.vp);
        vp.setOffscreenPageLimit(2);
        ll_search_result_container = findViewById(R.id.ll_search_result_container);
    }

    @Override
    protected void initDatas() {
        super.initDatas();
        //搜索结果展示ViwPager
        searchResultAdapter = new SearchResultAdapter(getActivity(),getSupportFragmentManager());
        //设置搜索框直接展开显示。左侧有放大镜(在搜索框中) 右侧有叉叉 可以关闭搜索框

        vp.setAdapter(searchResultAdapter);
        tabs.setupWithViewPager(vp);

        ArrayList<Integer> integers = new ArrayList<>();
        integers.add(0);
        integers.add(1);
        searchResultAdapter.setDatas(integers);
    }


    private void onSearchClick(String content) {
        searchView.setQuery(content,true);
        //是否进入界面就打开搜索栏，false为默认打开，默认为true
        searchView.setIconified(false);
        KeyboardUtil.hideKeyboard(this);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search_view);
        searchView = (SearchView) searchItem.getActionView();
        //可以在这里配置SearchView
        SearchView.SearchAutoComplete mSearchAutoComplete = searchView.findViewById(R.id.search_src_text);

        //设置输入框提示文字样式
        mSearchAutoComplete.setHintTextColor(getResources().getColor(android.R.color.black));//设置提示文字颜色
        mSearchAutoComplete.setTextColor(getResources().getColor(android.R.color.black));//设置内容文字颜色
        searchView.setIconified(false);
        searchView.onActionViewExpanded();
        //关闭监听器
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {

                //changeNormalView();
                return false;
            }
        });


        //设置搜索监听器
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                KeyboardUtil.hideKeyboard(SearchActivity.this);
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        //是否进入界面就打开搜索栏，false为默认打开，默认为true
        searchView.setIconified(false);

        SearchManager searchManager =
                (SearchManager) getSystemService(this.SEARCH_SERVICE);
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        return true;

    }


    private void performSearch(String data) {
        Log.d(TAG, "performSearch: "+data);
        //发布搜索Key
        EventBus.getDefault().post(new OnSearchKeyChangedEvent(data));
        changeSearchResultView();
    }

    private void changeSearchResultView() {
        ll_search_result_container.setVisibility(View.VISIBLE);
    }

    private void changeNormalView() {
        ll_search_result_container.setVisibility(View.GONE);
    }



}
