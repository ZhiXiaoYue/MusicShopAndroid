package com.example.jill.firsttry.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.jill.firsttry.R;
import com.example.jill.firsttry.model.Song;

import java.util.List;

public class SongCardAdapter extends RecyclerView.Adapter<SongCardAdapter.ViewHolder>{
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;
    private List<Song> mSongList;
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView songImage;
        TextView songName;

        ViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            songImage = itemView.findViewById(R.id.song_image_of_card);
            songName = itemView.findViewById(R.id.song_name_of_card);
        }
    }

    public SongCardAdapter(List<Song> songList) {
        mSongList = songList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.songcard_item,parent,false);
        return new ViewHolder(view);
    }

    /**
     * 绑定并设定点击事件（利用接口，将position传递给mainactivity）
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        Song song = mSongList.get(position);
        holder.songName.setText(song.getSname());
        Glide.with(mContext).load("http://58.87.73.51:8080/musicshop/"+song.getAlbumPic())
                .into(new SimpleTarget<GlideDrawable>() {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        holder.songImage.setImageDrawable(resource);
                    }
                });
        holder.songImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(v, position);
            }
        });
        holder.songName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSongList.size();
    }

}
