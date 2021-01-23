package com.busenamli.musicplayerdeneme;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

public class SongAdapter extends RecyclerView.Adapter <SongAdapter.ViewHolder> {

    private Context context;
    static ArrayList<MusicFiles> musicsFiles;

    public SongAdapter(Context context, ArrayList<MusicFiles> musicFiles) {
        this.context = context;
        this.musicsFiles = musicFiles;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Fragment ve layoutu bağlıyor
        View view = LayoutInflater.from(context).inflate(R.layout.songs_fragment_details,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        holder.songName.setText(musicsFiles.get(position).getTitle()); //Bu pozisyondaki şarkının Title'ını alıp ve şarkının ismini gösteriyor

        byte[] image = songsImage(musicsFiles.get(position).getData()); //songImage fonksiyonuyla şarkının resim dosyasını byte array'e dönüştürüyor

        //Glide kütüphanesiyle şarkı resmini ayarlıyor
        if (image!= null){
            Glide.with(context).asBitmap().load(image).into(holder.songImage);

        }else{
            Glide.with(context).asBitmap().load(R.drawable.ic_launcher_background).into(holder.songImage);

        }
        //Tıklanan pozisyondaki şarkının çalması
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,PlayerActivity.class);
                intent.putExtra("position", position); //Player Activity'de kullanabilmek için pozisyonu intent ile yolluyor
                context.startActivity(intent);
            }
        });

        //Menü seçeneğini kullanmak
       holder.options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                PopupMenu popupMenu = new PopupMenu(context,view);
                popupMenu.getMenuInflater().inflate(R.menu.options, popupMenu.getMenu());
                popupMenu.show();

                //Bir menü seçeneğine tılandığında ne yapılacak
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.delete_song: //Şarkıyı silmek için/ÇALIŞMADI

                                //Long.parseLong(musicFiles.get(i).getId());
                                deleteFile(position, view);
                                System.out.println("Çalıştı");
                                return true;
                            default:
                                return false;
                        }
                    }
                });
            }
        });

    }

    //Seçilen şarkıyı telefondan siliyor/ ÇALIŞMADI
   private void deleteFile(int position, View view){

        Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, Long.parseLong(musicsFiles.get(position).getId()));

       System.out.println("CONTENTURI " + contentUri);
        File file = new File(musicsFiles.get(position).getData());
       System.out.println("FILE" + file);
       /*boolean exists = file.exists();
       System.out.println("EXIST" + exists);*/
       boolean deleted = file.delete();
       System.out.println( "DELETED" + file.delete());

       //String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
       //System.out.println("BASEDIR " + baseDir);
       //File f = new File(baseDir + musicsFiles.get(position).getData());
       //System.out.println("FFFF " + f);

      //boolean d = f.delete();
       //System.out.println(d);

        if (deleted){

            context.getContentResolver().delete(contentUri, null,null);
            musicsFiles.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, musicsFiles.size());
            Snackbar.make(view, "File deleted", Snackbar.LENGTH_LONG).show();
        }else{
            Snackbar.make(view, "File can't be deleted", Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public int getItemCount() {
        return musicsFiles.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView songImage;
        TextView songName;
        ImageView options;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            songImage = itemView.findViewById(R.id.song_image);
            songName = itemView.findViewById(R.id.song_name);
            options = itemView.findViewById(R.id.options);
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

    //Şarkı listesi içinde arama yapma
    void searchList(ArrayList<MusicFiles> musicFilesArray){

        musicsFiles = new ArrayList<>();
        //Aranan harfler-kelimeler sonucu gösterilen şarkıları bu Array'e ekliyor ve bu listeden çalmasını sağlıyor
        musicsFiles.addAll(musicFilesArray);
        notifyDataSetChanged();
    }
}