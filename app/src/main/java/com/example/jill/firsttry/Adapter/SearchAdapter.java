package com.example.jill.firsttry.Adapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import com.example.jill.firsttry.R;
import com.example.jill.firsttry.Utils.DownloadCompany;
import com.example.jill.firsttry.model.Song;
import com.example.jill.firsttry.model.global_val.AppContext;
import java.util.ArrayList;
//import android.support.v7.app.AppCompatActivity;

public class SearchAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Song> songs;
    private AppContext appContext;

    public SearchAdapter(Context context, ArrayList<Song> songs,AppContext appContext) {
        this.context = context;
        this.songs = songs;
        this.appContext=appContext;
    }

    @Override
    public int getCount() {
        return songs.size();
    }

    @Override
    public Object getItem(int position) {
        return songs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @SuppressLint("SetTextI18n")
    @Override
    public View getView(final int position, View converView, ViewGroup parent) {
        ViewHolder viewHolder;

        //初始化ViewHolder
        if (converView == null) {
            converView = LayoutInflater.from(context).inflate(R.layout.search_listview_item, parent, false);
            viewHolder = new ViewHolder(converView);
            converView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) converView.getTag();
        }

        Song currentItem=(Song)getItem(position);
        viewHolder.songName.setText(currentItem.getSname());
        viewHolder.artistAndAlbum.setText(currentItem.getSingerName()+" - "+currentItem.getAlbum());

        // final View finalConverView = converView;

        //下载伴奏
        viewHolder.DownloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appContext.setSong((Song)getItem(position));
                // Log.d(TAG, "Button row pos click: " + appContext.getSong().getId());
                //将被点击的位置通知传给downLoadSongs函数，向服务器发送请求
                DownloadCompany downloadCompany=new DownloadCompany(context,(Song) getItem(position));
               // downloadCompany.downloadSong();
            }
        });

        return converView;

    }


    private class ViewHolder{
        TextView songName;
        TextView artistAndAlbum;
        Button DownloadButton;

        ViewHolder(View view){
            songName=view.findViewById(R.id.text_view_songname);
            DownloadButton=view.findViewById(R.id.download_button);
            artistAndAlbum=view.findViewById(R.id.text_view_artists_and_album);
        }
    }
}



