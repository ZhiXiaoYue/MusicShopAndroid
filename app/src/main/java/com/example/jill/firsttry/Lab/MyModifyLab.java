package com.example.jill.firsttry.Lab;

import android.util.Log;

import com.example.jill.firsttry.Utils.Consts;
import com.example.jill.firsttry.Utils.GetCompanyFromDir;
import com.example.jill.firsttry.model.LocalRecord;

import java.util.ArrayList;
import java.util.List;

import static android.support.constraint.Constraints.TAG;

public class MyModifyLab {
    private MyModifyLab sMyModifyLab;

    private List<LocalRecord> mKeys;

    public MyModifyLab get() {
        if (sMyModifyLab == null) {
            sMyModifyLab = new MyModifyLab();
        }

        return sMyModifyLab;
    }

    public MyModifyLab() {
        mKeys=new ArrayList<>();
        mKeys= GetCompanyFromDir.getSong(Consts.MODIFY_SONG_DIR);
        for (LocalRecord keys:mKeys){
        Log.d(TAG, keys.getSname());
    }

    }

    public List<LocalRecord> getCrimes() {
        return mKeys;
    }

}
