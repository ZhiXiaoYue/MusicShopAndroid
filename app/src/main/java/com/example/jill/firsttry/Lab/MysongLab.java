package com.example.jill.firsttry.Lab;

import android.util.Log;

import com.example.jill.firsttry.Utils.Consts;
import com.example.jill.firsttry.Utils.GetCompanyFromDir;
import com.example.jill.firsttry.model.LocalRecord;
import com.example.jill.firsttry.model.Song;

import java.util.ArrayList;
import java.util.List;

import static android.support.constraint.Constraints.TAG;

public class MysongLab {
    private MysongLab sMysongLab;

    private List<LocalRecord> mKeys;

    public MysongLab get() {
        if (sMysongLab == null) {
            sMysongLab = new MysongLab();
        }

        return sMysongLab;
    }

    public MysongLab() {
        mKeys=new ArrayList<>();
        mKeys= GetCompanyFromDir.getSong(Consts.SAVE_SONG_DIR);
        for (LocalRecord keys:mKeys){
        Log.d(TAG, keys.getSname());
    }

    }

    public List<LocalRecord> getCrimes() {
        return mKeys;
    }

}
