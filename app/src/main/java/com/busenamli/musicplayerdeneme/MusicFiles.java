package com.busenamli.musicplayerdeneme;

import android.util.Log;

//Şarkıların bilgilerinin vs. olduğu sınıf, bundan objeler ürettim
public class MusicFiles {
    private String data;
    private String title;
    private String artist;
    private String album;
    private String duration;
    private String id;

    public MusicFiles(String data, String title, String artist, String album, String duration, String id) {
        this.data = data;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
        this.id = id;

    }

    public MusicFiles() {
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
