package com.example.jill.firsttry.Adapter;

import android.content.Context;
import android.support.v4.app.FragmentManager;

import com.example.jill.firsttry.R;
import com.example.jill.firsttry.model.Song;



/**
 * 歌曲列表adapter
 *
 */

public class SongAdapter extends BaseQuickRecyclerViewAdapter<Song> {

    private final FragmentManager fragmentManager;

    public SongAdapter(Context context, int layoutId, FragmentManager fragmentManager) {
        super(context, layoutId);
        this.fragmentManager=fragmentManager;
    }

    @Override
    protected void bindData(ViewHolder holder, int position, final Song data) {
        holder.setText(R.id.tv_title,data.getSname());
        holder.setText(R.id.tv_info,data.getSingerName()+" - "+data.getAlbum());
    }
}
