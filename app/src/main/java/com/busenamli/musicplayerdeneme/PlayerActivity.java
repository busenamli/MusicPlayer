package com.busenamli.musicplayerdeneme;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.busenamli.musicplayerdeneme.AlbumActivityAdapter.albumFiles;
import static com.busenamli.musicplayerdeneme.MainActivity.musicFiles;
import static com.busenamli.musicplayerdeneme.MainActivity.repeatBool;
import static com.busenamli.musicplayerdeneme.MainActivity.shuffleBool;
import static com.busenamli.musicplayerdeneme.SongAdapter.musicsFiles;

public class PlayerActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener {

    TextView songName,songArtist, durationNow, durationTotal;
    SeekBar seekbar;
    ImageView songImage, backButton, menuButton, shuffleButton, repeatButton, nextButton, previousButton;
    FloatingActionButton playPauseButton;
    int position = -1;
    static ArrayList<MusicFiles> songsList = new ArrayList<>();
    static Uri uri;
    static MediaPlayer mediaPlayer;
    Handler handler = new Handler();
    private Thread playThread, previousThread, nextThread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        songName = findViewById(R.id.song_title);
        songArtist = findViewById(R.id.song_artist);
        durationNow = findViewById(R.id.duration_now);
        durationTotal = findViewById(R.id.duration_total);
        seekbar = findViewById(R.id.seekbar);
        songImage = findViewById(R.id.player_image);
        backButton = findViewById(R.id.back_button);
        menuButton = findViewById(R.id.menu_button);
        shuffleButton = findViewById(R.id.shuffle_button);
        repeatButton = findViewById(R.id.repeat_button);
        nextButton = findViewById(R.id.next_button);
        previousButton = findViewById(R.id.previous_button);
        playPauseButton = findViewById(R.id.play_pause_button);

        getIntentData();
        seekbarAction();
        mediaPlayer.setOnCompletionListener(this);

        //Karışık çalma
        shuffleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (shuffleBool == false) {
                    shuffleBool = true; //Karışık çalma açık
                    shuffleButton.setImageResource(R.drawable.ic_shuffle_on); // mavi renkte buton

                } else {
                    shuffleBool = false; //Karışık çalma kapalı
                    shuffleButton.setImageResource(R.drawable.ic_shuffle); // siyah renkte buton
                }
            }
        });

        repeatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (repeatBool == false) {
                    repeatBool = true; //tekrar açık
                    repeatButton.setImageResource(R.drawable.ic_repeat_on);//mavi renkte buton
                } else {
                    repeatBool = false;//tekrar kapalı
                    repeatButton.setImageResource(R.drawable.ic_repeat);//siyah renkte buton
                }
            }
        });

    }

    public int getRandom(int size){
        Random random = new Random();
        return random.nextInt(size + 1);
    }

    @Override
    protected void onResume() {
        playThreadButton();
        previousThreadButton();
        nextThreadButton();
        super.onResume();
    }

    //Durdur-Başlat
    private void playThreadButton(){

        playThread = new Thread(){


            @Override
            public void run() {
                super.run();
                playPauseButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (mediaPlayer.isPlaying()){
                            playPauseButton.setImageResource(R.drawable.ic_play);
                            mediaPlayer.pause();
                            seekbar.setMax(mediaPlayer.getDuration()/1000);
                            songAction();
                        }
                        else{
                            playPauseButton.setImageResource(R.drawable.ic_pause);
                            mediaPlayer.start();
                            seekbar.setMax(mediaPlayer.getDuration()/1000);
                            songAction();
                        }
                    }
                });
            }
        };
        playThread.start();
    }

    private void previousThreadButton() {

        //Önceki şarkı
        previousThread = new Thread(){
            @Override
            public void run() {
                super.run();
                previousButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        //Şarkı çalıyorsa
                        if (mediaPlayer.isPlaying()){

                            mediaPlayer.stop();
                            mediaPlayer.release();

                            //Karışık çalma ve tekrar açık mı kapalı mı
                            if(shuffleBool == true && repeatBool == false){

                                position = getRandom(songsList.size()-1); //Karışık çalma açıksa getRandom metoduyla bir pozisyon belirle
                            }
                            //Karışık çalma ve tekrar açık değil
                            else if(shuffleBool == false && repeatBool == false){

                                //İlk şarkı çalıyorsa sıradaki şarkıyı listenin son şarkısı yap
                                if (position - 1 < 0 ){
                                    position = (songsList.size() - 1);
                                }
                                //İlk şarkı değilse bir önceki şarkı
                                else{
                                    position = position - 1;
                                }
                            }

                            uri = Uri.parse(songsList.get(position).getData());
                            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                            getMetaData(uri);
                            seekbar.setMax(mediaPlayer.getDuration()/1000);
                            songAction();
                            mediaPlayer.setOnCompletionListener(PlayerActivity.this);
                            playPauseButton.setImageResource(R.drawable.ic_pause);
                            mediaPlayer.start();
                        }
                        //Şarkı duraklatıldıysa
                        else{

                            mediaPlayer.stop();
                            mediaPlayer.release();

                            if(shuffleBool == true && repeatBool == false){

                                position = getRandom(songsList.size()-1);
                            }
                            else if(shuffleBool == false && repeatBool == false){

                                if (position - 1 < 0 ){
                                    position = (songsList.size() - 1);
                                }else{
                                    position = position - 1;
                                }
                            }

                            uri = Uri.parse(songsList.get(position).getData());
                            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                            getMetaData(uri);
                            seekbar.setMax(mediaPlayer.getDuration()/1000);
                            songAction();
                            mediaPlayer.setOnCompletionListener(PlayerActivity.this);
                            playPauseButton.setImageResource(R.drawable.ic_pause);
                            mediaPlayer.start();
                        }


                    }
                });
            }
        };
        previousThread.start();

    }

    //Sonraki şarkı
    private void nextThreadButton(){

        nextThread = new Thread(){
            @Override
            public void run() {
                super.run();
                nextButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        //Şarkı çalıyorsa
                        if (mediaPlayer.isPlaying()){

                            mediaPlayer.stop();
                            mediaPlayer.release();

                            //Karışık çalma ve tekrar açık mı kapalı mı
                            if(shuffleBool == true && repeatBool == false){

                                position = getRandom(songsList.size()-1);//Karışık çalma açıksa getRandom metoduyla bir pozisyon belirle

                            }
                            //Karışık çalma ve tekrar açık değil
                            else if(shuffleBool == false && repeatBool == false){

                                //Şarkı listesi bittiğinde başa dönmesini sağlamak için sıradaki pozisyonun eleman sayısına kalanını aldım
                                // böylece son şarkıda kalan 0 olduğunda başa dönecek
                                position = ((position + 1) % songsList.size());
                            }

                            uri = Uri.parse(songsList.get(position).getData());
                            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                            getMetaData(uri);
                            seekbar.setMax(mediaPlayer.getDuration()/1000);
                            songAction();
                            mediaPlayer.setOnCompletionListener(PlayerActivity.this);
                            playPauseButton.setImageResource(R.drawable.ic_pause);
                            mediaPlayer.start();
                        }
                        //Şarkı duraklatıldıysa
                        else{

                            mediaPlayer.stop();
                            mediaPlayer.release();
                            if(shuffleBool == true && repeatBool == false){

                                position = getRandom(songsList.size()-1);
                            }
                            else if(shuffleBool == false && repeatBool == false){

                                position = ((position + 1) % songsList.size());
                            }

                            uri = Uri.parse(songsList.get(position).getData());
                            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                            getMetaData(uri);
                            seekbar.setMax(mediaPlayer.getDuration()/1000);
                            songAction();
                            mediaPlayer.setOnCompletionListener(PlayerActivity.this);
                            playPauseButton.setImageResource(R.drawable.ic_pause);
                            mediaPlayer.start();
                        }

                    }
                });
            }
        };
        nextThread.start();
    }

    //Şarkının çalması
    public void songAction(){

        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Şarkı çalıyorsa
                if (mediaPlayer != null){
                    int positionNow = mediaPlayer.getCurrentPosition()/1000;
                    seekbar.setProgress(positionNow);
                    durationNow.setText(formattedTime(positionNow)); //Şu anki şarkı daikasını gösteiyor
                }
                handler.postDelayed(this,1000); //Saniye saniye tekrar etmesini sağlayacak
            }
        });

    }

    public void seekbarAction(){

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(mediaPlayer != null && b){
                    mediaPlayer.seekTo(i * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        songAction();
    }

    public String formattedTime(int positionNow){

        //Text'lerde süreyi dakika ve saniye olarak göstermek için
        String totalOut = "";
        String totalNew = "";
        String seconds = String.valueOf(positionNow % 60);
        String minutes = String.valueOf(positionNow / 60);
        totalOut = minutes + ":" + seconds;
        totalNew = minutes + ":" + "0" + seconds;
        if (seconds.length() == 1){
            return  totalNew;
        }
        else{
            return totalOut;
        }
    }

    public void getIntentData(){

        position = getIntent().getIntExtra("position",-1);//Song Adapter veya Album Adapter'da yolladığımız pozisyonu intent ile alıyor
        String albumDetails = getIntent().getStringExtra("albumDetails"); //Album Adapter'da yolladığımız String'i intent ile alıyor
        //songsList = new ArrayList<>();


        if (albumDetails != null && albumDetails.equals("albumDetails")){

            songsList = albumFiles;//Çalacak şarkı listesi albümdeki şarkılar
        }
        else {
            songsList = musicsFiles; //Boş ise normal şarkı listesi çalar
        }

        //Şarkı listesi boş değilse
        if (songsList != null){

            playPauseButton.setImageResource(R.drawable.ic_pause); //Durdur-başlat butonu durdur resmini gösterir
            uri = Uri.parse(songsList.get(position).getData()); // Seçilen pozisyondaki şarkının urisi alınır
        }

        //mediaplayer boş ise
        if (mediaPlayer == null){

            //alınan urilerle bir mediaplayer oluşturulur
            mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
            mediaPlayer.start();

        }//Boş değilse
        else{
            //Durdurulup yeniden açılır
            mediaPlayer.stop();
            mediaPlayer.release();
            // alınan urilerle bir mediaplayer oluşturulur ve başlatılır
            mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
            mediaPlayer.start();

        }

        seekbar.setMax(mediaPlayer.getDuration()/1000); //seekbar'ın max seviyesini ayarlıyor
        getMetaData(uri);
    }

    //Şarkı datalarını alıyor
    private void getMetaData(Uri mediaUri){

        MediaMetadataRetriever mediaRetriever = new MediaMetadataRetriever();
        mediaRetriever.setDataSource(mediaUri.toString());
        int songTotal = Integer.parseInt(songsList.get(position).getDuration())/1000; //Şarkının tüm süresi
        durationTotal.setText(formattedTime(songTotal));//Text'i süre olarak ayarlıyor

        byte[] songPicture = mediaRetriever.getEmbeddedPicture(); //şarkının resmini byte array'e dönüştürdük

        //Şarkı resmi boş değilse resmi gösterir
        if (songPicture != null){
            Glide.with(PlayerActivity.this).asBitmap().load(songPicture).into(songImage);
            Glide.with(getApplicationContext()).asBitmap().load(songPicture).into(MainActivity.main_song_image);

        }
        //Boş ise default resim
        else{
            Glide.with(PlayerActivity.this).asBitmap().load(R.drawable.ic_menu).into(songImage);
            Glide.with(getApplicationContext()).asBitmap().load(R.drawable.ic_launcher_background).into(MainActivity.main_song_image);
        }

        //Şarkı ve Şarkıcı Text'lerini ayarlıyor
        songName.setText(songsList.get(position).getTitle());
        MainActivity.main_song_name.setText(songsList.get(position).getTitle());
        songArtist.setText(songsList.get(position).getArtist());
        MainActivity.main_artist.setText(songsList.get(position).getArtist());


    }

    //Şarkı bitince sıradaki şarkıyla devam etme
    @Override
    public void onCompletion(MediaPlayer mp) {

        //MediaPlayer çalıyorsa
        if (mediaPlayer.isPlaying()){

            mediaPlayer.stop();
            mediaPlayer.release();
            //Şarkı listesi bittiğinde başa dönmesini sağlamak için sıradaki pozisyonun eleman sayısına kalanını aldım
            // böylece son şarkıda kalan 0 olduğunda başa dönecek
            position = ((position + 1) % songsList.size());
            uri = Uri.parse(songsList.get(position).getData());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            getMetaData(uri); //Şarkının urisini alıp fonksiyonu kullanarak şarkı süresi, ismi, şarkıcı, resmi alıyor
            seekbar.setMax(mediaPlayer.getDuration()/1000); //Seekbar pozisyonu ayarlandı
            songAction(); //Fonksiyon ile şarkı çalıyor
            playPauseButton.setImageResource(R.drawable.ic_pause); //Başlat-durdur butonu beklet ile değişiyor
            mediaPlayer.start();
        }
        //MediaPlayer çalmıyorsa
        else{

            mediaPlayer.stop();
            mediaPlayer.release();
            position = ((position + 1) % songsList.size());
            uri = Uri.parse(songsList.get(position).getData());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            getMetaData(uri);
            seekbar.setMax(mediaPlayer.getDuration()/1000);
            songAction();
            playPauseButton.setImageResource(R.drawable.ic_pause);
        }

        if (mediaPlayer != null){
            mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(this);
        }
    }
}