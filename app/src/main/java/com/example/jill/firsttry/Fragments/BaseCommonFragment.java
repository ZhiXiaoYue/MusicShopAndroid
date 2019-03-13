package com.example.jill.firsttry.Fragments;


import com.example.jill.firsttry.Utils.SharedPreferencesUtil;


/**
 * Created by smile on 02/03/2018.
 */

public abstract class BaseCommonFragment extends BaseFragment {
    protected SharedPreferencesUtil sp;

    @Override
    protected void initViews() {
        super.initViews();
        sp = SharedPreferencesUtil.getInstance(getActivity().getApplicationContext());
    }


}
