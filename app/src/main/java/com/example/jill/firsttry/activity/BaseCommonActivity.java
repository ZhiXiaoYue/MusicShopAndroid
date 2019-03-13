package com.example.jill.firsttry.activity;


import com.example.jill.firsttry.Utils.SharedPreferencesUtil;

import butterknife.ButterKnife;

/**
 * Created by smile on 02/03/2018.
 */

public class BaseCommonActivity extends BaseActivity {

    protected SharedPreferencesUtil sp;


    @Override
    protected void initViews() {
        super.initViews();
        ButterKnife.bind(this);
        sp = SharedPreferencesUtil.getInstance(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
