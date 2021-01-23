package com.busenamli.musicplayerdeneme;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.busenamli.musicplayerdeneme.MainActivity.*;

public class SongsFragment extends Fragment {

    RecyclerView recyclerView;
    static SongAdapter songAdapter;

    public static SongsFragment newInstance(){
        return new SongsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //RecyclerView ile Fragment'ı bağladık
        View view = inflater.inflate(R.layout.songs_fragment,container,false);
        recyclerView = view.findViewById(R.id.recylerview);
        recyclerView.setHasFixedSize(true);

        //Şarkı listesini göstermek için Adapter'ı kullandık
        if (!(musicFiles.size()<1)){
            songAdapter = new SongAdapter(getContext(), musicFiles);
            recyclerView.setAdapter(songAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL,false));
        }

        return view;
    }
}
