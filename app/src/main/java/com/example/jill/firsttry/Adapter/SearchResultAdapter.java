package com.example.jill.firsttry.Adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.example.jill.firsttry.Fragments.SearchSongResultFragment;


/**
 * 搜索结果ViewPager
 * Created by smile on 2018/5/26.
 */

public class SearchResultAdapter extends BaseFragmentPagerAdapter<Integer> {
    private static String[] titleNames = {"单曲", "歌手"};

    public SearchResultAdapter(Context context, FragmentManager fm) {
        super(context, fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return SearchSongResultFragment.newInstance();
        } else {
            //TODO 更多搜索
            return  SearchSongResultFragment.newInstance();
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titleNames[position];
    }
}