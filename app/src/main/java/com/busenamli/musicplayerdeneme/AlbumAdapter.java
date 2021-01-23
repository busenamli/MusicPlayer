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

public class AlbumAdapter extends RecyclerView.Adapter <AlbumAdapter.ViewHolder> {

    private Context context;
    private ArrayList<MusicFiles> albumFiles;
    View view;

    public AlbumAdapter(Context context, ArrayList<MusicFiles> albumFiles) {
        this.context = context;
        this.albumFiles = albumFiles;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Fragment ve layoutu bağlıyor
        view = LayoutInflater.from(context).inflate(R.layout.album_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        holder.album_name.setText(albumFiles.get(position).getAlbum()); //Bu pozisyondaki şarkının albüm ismini alıp gösteriyor
        byte[] image = songsImage(albumFiles.get(position).getData()); //songImage fonksiyonuyla şarkının resim dosyasını byte array'e dönüştürüyor
        //Glide kütüphanesiyle şarkı resmini ayarlıyor
        if (image!= null){
            Glide.with(context).asBitmap().load(image).into(holder.album_image);
        }else{
            Glide.with(context).asBitmap().load(R.drawable.ic_launcher_background).into(holder.album_image);
        }
        //Bir albüme tıklandığında
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,AlbumActivity.class);
                intent.putExtra("album_name", albumFiles.get(position).getAlbum());//Albüm ismini Album Activity'de kullanmak için intent ile yolluyor
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
        TextView album_name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            album_image = itemView.findViewById(R.id.album_image);
            album_name = itemView.findViewById(R.id.album_name);
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
