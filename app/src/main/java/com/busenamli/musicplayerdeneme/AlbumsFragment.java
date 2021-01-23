package com.busenamli.musicplayerdeneme;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.busenamli.musicplayerdeneme.MainActivity.albumFiles;

public class AlbumsFragment extends Fragment {

    RecyclerView recyclerView;
    AlbumAdapter albumAdapter;

    public static AlbumsFragment newInstance(){
        return new AlbumsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //RecyclerView ile Fragment'ı bağladık
        View view = inflater.inflate(R.layout.albums_fragment,container,false);
        recyclerView = view.findViewById(R.id.recylerview);
        recyclerView.setHasFixedSize(true);

        //Albüm listesini göstermek için Adapter'ı kullandık
        if (!(albumFiles.size()<1)){
            albumAdapter = new AlbumAdapter(getContext(), albumFiles);
            recyclerView.setAdapter(albumAdapter);
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        }

        return view;
    }
}
