package com.example.jill.firsttry.Lab;

import android.util.Log;

import com.example.jill.firsttry.Utils.GetCompanyFromDir;
import com.example.jill.firsttry.model.Song;

import java.util.ArrayList;
import java.util.List;

import static android.support.constraint.Constraints.TAG;

public class MysongLab {
    private MysongLab sMysongLab;

    private List<Song> mKeys;

    public MysongLab get() {
        if (sMysongLab == null) {
            sMysongLab = new MysongLab();
        }

        return sMysongLab;
    }

    public MysongLab() {
        mKeys=new ArrayList<>();
        mKeys= GetCompanyFromDir.getComp("/mnt/sdcard/MusicShopDownLoad/MySongs/");
        for (Song keys:mKeys){
        Log.d(TAG, keys.getSname());
    }

    }

    public List<Song> getCrimes() {
        return mKeys;
    }

}
