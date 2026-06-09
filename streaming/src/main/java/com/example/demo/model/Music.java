package com.example.demo.model;

public class Music {
    private String id;
    private String name;
    private String artist;
    private String genre;
    private int durationSeconds;

    public Music() {}
    public Music(String id, String name, String artist, String genre, int durationSeconds) {
        this.id = id;
        this.name = name;
        this.artist = artist;
        this.genre = genre;
        this.durationSeconds = durationSeconds;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getArtist() { return artist; }
    public void setArtist(String artist) { this.artist = artist; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    public int getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(int durationSeconds) { this.durationSeconds = durationSeconds; }
}