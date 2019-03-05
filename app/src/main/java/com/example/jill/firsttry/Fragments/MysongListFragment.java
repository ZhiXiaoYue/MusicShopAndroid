package com.example.jill.firsttry.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.jill.firsttry.Lab.MysongLab;
import com.example.jill.firsttry.R;
import com.example.jill.firsttry.activity.ListenActivity;
import com.example.jill.firsttry.model.Song;
import com.example.jill.firsttry.others.AppContext;

import java.io.File;
import java.util.List;
import java.util.Objects;

import static android.support.constraint.Constraints.TAG;

public class MysongListFragment extends Fragment {
    private RecyclerView mCompanyRecyclerView;
    private AppContext appContext;
    private MysongLab mysongLab;
    //每次点击伴奏这个fragment都要进行刷新，isGetData作为刷新标识
    //private boolean isGetData=false;
    List<Song> keysList;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "调用了onCreateView");
        View view = inflater.inflate(R.layout.fragment_songs_list, container, false);

        mCompanyRecyclerView = view
                .findViewById(R.id.crime_recycler_view);
        mCompanyRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        appContext = ((AppContext) Objects.requireNonNull(getActivity()).getApplication());
        this.mysongLab=new MysongLab();
        updateUI();
        return view;
    }

    private void updateUI() {
        keysList = mysongLab.getCrimes();
        MysongListFragment.MysongCrimeAdapter mAdapter = new MysongListFragment.MysongCrimeAdapter(keysList);
        mCompanyRecyclerView.setAdapter(mAdapter);
    }

    private class MysongCrimeHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private Song keys;

        private TextView mysongNameTextView;
        private TextView mysongAlbAndArtistsTextView;
        private Button mysongDeleteButton;

        MysongCrimeHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_mysong, parent, false));
            itemView.setOnClickListener(this);

            mysongNameTextView = itemView.findViewById(R.id.text_view_mysong_name);
            mysongAlbAndArtistsTextView = itemView.findViewById(R.id.text_view_mysong_artists_and_album);
            mysongDeleteButton=itemView.findViewById(R.id.mysongdelete_button);
        }

        @SuppressLint("SetTextI18n")
        public void bind(Song keys) {
            this.keys=keys;
            mysongNameTextView.setText(keys.getSname());
            mysongAlbAndArtistsTextView.setText(keys.getSingerName()+"-"+keys.getAlbum());
        }

        @Override
        public void onClick(View view) {
            appContext.setSong(keys);
            Intent intent = new Intent(getActivity(), ListenActivity.class);
            startActivity(intent);
        }

    }

    private class MysongCrimeAdapter extends RecyclerView.Adapter<MysongListFragment.MysongCrimeHolder> {

        private List<Song> keysList;

        MysongCrimeAdapter(List<Song> keysList) {
            this.keysList=keysList;
        }

        @NonNull
        @Override
        public MysongListFragment.MysongCrimeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new MysongListFragment.MysongCrimeHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull MysongListFragment.MysongCrimeHolder holder, final int position) {
            final Song keys = keysList.get(position);
            holder.bind(keys);
            holder.mysongDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    File file=new File("/mnt/sdcard/MusicShopDownLoad/MySongs/"+keys.getSname() + "-" + keys.getSingerName() + "-" +keys.getAlbum()+"-"+keys.getSid()+".mp3");
                    file.delete();
                    Log.e(keys.getSname() + "-" + keys.getSingerName() + "-" +keys.getAlbum()+"-"+keys.getSid(),"5");
                    keysList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, keysList.size());
                }
            });
        }

        @Override

        public int getItemCount() {
            return keysList.size();
        }
    }
}
