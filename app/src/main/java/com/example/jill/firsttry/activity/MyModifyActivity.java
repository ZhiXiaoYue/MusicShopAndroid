package com.example.jill.firsttry.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.jill.firsttry.Fragments.MyModifyListFragment;
import com.example.jill.firsttry.Fragments.MysongListFragment;
import com.example.jill.firsttry.R;

/**
 * 我的歌曲
 */
public class MyModifyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置状态栏可见；
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        setContentView(R.layout.activity_fragement);
        setTitle("我的修改歌曲");

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            fragment = createFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }
    protected Fragment createFragment() {
        return new MyModifyListFragment();

    }
}
