package com.busenamli.musicplayerdeneme;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class AlbumActivityAdapter extends RecyclerView.Adapter<AlbumActivityAdapter.ViewHolder> {

    private Context context;
    static ArrayList<MusicFiles> albumFiles;
    View view;

    public AlbumActivityAdapter(Context context, ArrayList<MusicFiles> albumFiles) {
        this.context = context;
        this.albumFiles = albumFiles;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        view = LayoutInflater.from(context).inflate(R.layout.songs_fragment_details,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        holder.album_song_name.setText(albumFiles.get(position).getTitle()); //Bu pozisyondaki şarkının ismini alıp gösteriyor
        byte[] image = songsImage(albumFiles.get(position).getData()); //songImage fonksiyonuyla şarkının resim dosyasını byte array'e dönüştürüyor
        //Glide kütüphanesiyle şarkı resmini ayarlıyor
        if (image!= null){
            Glide.with(context).asBitmap().load(image).into(holder.album_image);
        }else{
            Glide.with(context).asBitmap().load(R.drawable.ic_launcher_background).into(holder.album_image);
        }
        //Bir şarkıya tıklandığında
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PlayerActivity.class);

                //albümü ve pozisyonu PlayerActivity'de kullanmak için intent ile gönderiyor
                intent.putExtra("albumDetails", "albumDetails");
                intent.putExtra("position", position);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return albumFiles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView album_image;
        TextView album_song_name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            album_image = itemView.findViewById(R.id.song_image);
            album_song_name = itemView.findViewById(R.id.song_name);
        }
    }

    //Şarkının resmini çekebilmek için uri'sini alıp byte array'e dönüştürüyor ve bu array'i döndürüyor
    private byte[] songsImage(String uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] images = retriever.getEmbeddedPicture();
        retriever.release();

        return images;

    }
}
