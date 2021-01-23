package com.busenamli.musicplayerdeneme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import static com.busenamli.musicplayerdeneme.PlayerActivity.mediaPlayer;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    static ImageView main_song_image;
    static TextView main_song_name, main_artist;
    static FloatingActionButton main_play_pause;
    static ArrayList<MusicFiles> musicFiles = new ArrayList<>();
    static ArrayList<MusicFiles> albumFiles = new ArrayList<>();
    static boolean shuffleBool = false;
    static boolean repeatBool = false;
    private Thread playThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        main_song_image = findViewById(R.id.main_song_image);
        main_song_name = findViewById(R.id.main_song_name);
        main_artist = findViewById(R.id.main_artist);
        main_play_pause = findViewById(R.id.main_play_pause);
        main_play_pause.setImageResource(R.drawable.ic_pause);

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);

        //ViewPager ile TabLayout'u birbirine bağladık
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);


        //Kullanıcı izni var mı yok mu kontrol ediyor yok ise izin istiyor var ise müzik dosyalarını alıyor
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            musicFiles = getAllFiles(this);
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        playThreadButton();
    }

    public void playThreadButton(){

        playThread = new Thread(){


            @Override
            public void run() {
                super.run();
                main_play_pause.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (mediaPlayer.isPlaying()){
                            main_play_pause.setImageResource(R.drawable.ic_play);
                            mediaPlayer.pause();

                        }
                        else{
                            main_play_pause.setImageResource(R.drawable.ic_pause);
                            mediaPlayer.start();
                        }
                    }
                });
            }
        };
        playThread.start();

    }

    //İzin işlemi
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //İstediğimiz izin koduna eşitse müzik dosyalarını alıyor
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                musicFiles = getAllFiles(this);

            }
        }
        //Eşit değilse izin istiyor
        else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

    }


   //Müzik dosyalarını alma
    public static ArrayList<MusicFiles> getAllFiles(Context context) {

        ArrayList<String> albumsName = new ArrayList<>();//Albumler için geçici liste
        ArrayList<MusicFiles> tempAudioList = new ArrayList<>();//Şarkılar için geçici liste
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;//Şarkı urisini alıyor

        //String dizisine şarkı adı vs. atanıyor
        String[] projection = {MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media._ID};


        //Dosyaları tek tek gezebilmek için cursor kullandım
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, MediaStore.Audio.Media.TITLE);

        if (cursor != null) {
            //Şarkı ismi vs. bilgileri ile yeni bir MusicFiles objesi oluşturdum
            while (cursor.moveToNext()) {

                String title = cursor.getString(0);
                String album = cursor.getString(1);
                String artist = cursor.getString(2);
                String duration = cursor.getString(3);
                String data = cursor.getString(4);
                String id = cursor.getString(5);

                MusicFiles musicFiles = new MusicFiles(data, title, artist, album, duration, id);
                Log.e("data : " + data, "Album : " + album);
                tempAudioList.add(musicFiles); //Geçici listeye şarkıları ekliyor

                //Aynı albümü tekrar göstermeden şarkıları aynı albüm listesine ekliyor
                if (!(albumsName.contains(album))){
                    albumFiles.add(musicFiles);
                    albumsName.add(album);
                }

            }
            cursor.close();
        }

        return tempAudioList;
    }

    //Arama çubuğu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.search, menu);
        MenuItem menuItem = menu.findItem(R.id.searchbar);

        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        //Yazılanı küçük karaktere dönüştürüyor
        String input = newText.toLowerCase();
        ArrayList<MusicFiles> musics = new ArrayList<>(); // Aranan şarkılar için bir liste

        //Tüm müzik dosyalarını geziyor
        for (MusicFiles song:musicFiles){
            //Şarkıların isimleri küçüm karaktere dönüştürülüyor ve şarkı ismi arananları içeriyorsa aşağıda listeleniyor
            if (song.getTitle().toLowerCase().contains(input)){
                musics.add(song);
            }
        }

        //Adapter ile bu şarkıları gösteriyor
        SongsFragment.songAdapter.searchList(musics);

        return true;
    }
}
