package com.example.jill.firsttry.Lab;

import android.util.Log;

import com.example.jill.firsttry.Utils.GetCompanyFromDir;
import com.example.jill.firsttry.model.Song;

import java.util.ArrayList;
import java.util.List;

import static android.support.constraint.Constraints.TAG;

public class CompanyLab {
    private CompanyLab sCompanyLab;

    private List<Song> mKeys;

    public CompanyLab get() {
        if (sCompanyLab == null) {
            sCompanyLab = new CompanyLab();
        }

        return sCompanyLab;
    }

     public CompanyLab() {
        mKeys=new ArrayList<>();
        mKeys= GetCompanyFromDir.getComp("/mnt/sdcard/MusicShopDownLoad/Songs/");
        for (Song keys:mKeys){
            Log.d(TAG, keys.getSname());
        }

    }

    public List<Song> getCrimes() {
        return mKeys;
    }

}
