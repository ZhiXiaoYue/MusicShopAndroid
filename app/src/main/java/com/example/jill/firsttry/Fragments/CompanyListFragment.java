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

import com.example.jill.firsttry.Lab.CompanyLab;
import com.example.jill.firsttry.R;
import com.example.jill.firsttry.Utils.Consts;
import com.example.jill.firsttry.activity.ManyActivity;
import com.example.jill.firsttry.model.Song;
import com.example.jill.firsttry.model.global_val.AppContext;

import java.io.File;
import java.util.List;
import java.util.Objects;

import static android.support.constraint.Constraints.TAG;

public class CompanyListFragment extends Fragment {
    private RecyclerView mCompanyRecyclerView;
    private AppContext appContext;
    private CompanyLab companyLab;
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
        this.companyLab=new CompanyLab();
        updateUI();
        return view;
    }

    private void updateUI() {
        keysList = companyLab.getCrimes();
        ComCrimeAdapter mAdapter = new ComCrimeAdapter(keysList);
        mCompanyRecyclerView.setAdapter(mAdapter);
    }

    private class ComCrimeHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private Song keys;

        private TextView comNameTextView;
        private TextView comAlbAndArtistsTextView;
        private Button comDeleteButton;

        ComCrimeHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_company, parent, false));
            itemView.setOnClickListener(this);

            comNameTextView = itemView.findViewById(R.id.text_view_com_name);
            comAlbAndArtistsTextView = itemView.findViewById(R.id.text_view_com_artists_and_album);
            comDeleteButton=itemView.findViewById(R.id.comdelete_button);
        }

        @SuppressLint("SetTextI18n")
        public void bind(Song keys) {
            this.keys=keys;
            comNameTextView.setText(keys.getSname());
            comAlbAndArtistsTextView.setText(keys.getSingerName()+"-"+keys.getAlbum());
        }

        @Override
        public void onClick(View view) {
            ManyActivity.actionStart(getContext(),keys);
        }

    }

    private class ComCrimeAdapter extends RecyclerView.Adapter<ComCrimeHolder> {

        private List<Song> keysList;

        ComCrimeAdapter(List<Song> keysList) {
            this.keysList=keysList;
        }

        @NonNull
        @Override
        public ComCrimeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new ComCrimeHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull ComCrimeHolder holder, final int position) {
           final Song keys = keysList.get(position);
            holder.bind(keys);
            holder.comDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    File file=new File(Consts.SONG_DIR+keys.getSname() + "-" + keys.getSingerName() + "-" + keys.getAlbum() + "-" + keys.getSid() + ".mp3");
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
