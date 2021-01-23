package com.busenamli.musicplayerdeneme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import static com.busenamli.musicplayerdeneme.MainActivity.musicFiles;

public class AlbumActivity extends AppCompatActivity {

    RecyclerView album_recyclerview;
    ImageView albumImage;
    String album_name;
    ArrayList<MusicFiles> albumSongs = new ArrayList<>();
    AlbumActivityAdapter albumActivityAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        album_recyclerview = findViewById(R.id.album_recylerview);
        albumImage = findViewById(R.id.albumImage);

        album_name = getIntent().getStringExtra("album_name");//Album Adapter'da yolladığımız albüm adını intent ile alıyor

        //Albüm listesine şarkıları ekliyoruz
        int j = 0;
        for(int i = 0; i < musicFiles.size(); i++){
            if (album_name.equals(musicFiles.get(i).getAlbum())) {
                albumSongs.add(j, musicFiles.get(i));
                j++;
            }
        }

        //Albüm kapak resmi için eklenen ilk şarkının resmini alıyor
        byte[] image = albumImage(albumSongs.get(0).getData());

        if (image!= null){
            Glide.with(this).asBitmap().load(image).into(albumImage);
        }else{
            Glide.with(this).asBitmap().load(R.drawable.ic_launcher_background).into(albumImage);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (albumSongs.size() >= 1){

            albumActivityAdapter =  new AlbumActivityAdapter(this,albumSongs);
            album_recyclerview.setAdapter(albumActivityAdapter);
            album_recyclerview.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL,false));

        }
    }

    //Şarkının resmini çekebilmek için uri'sini alıp byte array'e dönüştürüyor ve bu array'i döndürüyor
    private byte[] albumImage(String uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] images = retriever.getEmbeddedPicture();
        retriever.release();

        return images;

    }
}