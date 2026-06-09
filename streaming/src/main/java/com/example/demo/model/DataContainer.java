package com.example.demo.model;

import java.util.List;

public class DataContainer {
    private List<User> users;
    private List<Music> musics;
    private List<Playlist> playlists;

    public List<User> getUsers() { return users; }
    public void setUsers(List<User> users) { this.users = users; }
    public List<Music> getMusics() { return musics; }
    public void setMusics(List<Music> musics) { this.musics = musics; }
    public List<Playlist> getPlaylists() { return playlists; }
    public void setPlaylists(List<Playlist> playlists) { this.playlists = playlists; }
}