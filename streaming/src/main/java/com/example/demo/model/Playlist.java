package com.example.demo.model;

import java.util.ArrayList;
import java.util.List;

public class Playlist {

    private String id;
    private String name;
    private List<String> musicIds = new ArrayList<>();

    public Playlist() {}

    public Playlist(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getMusicsIds() {
        return musicIds;
    }

    public void setMusicIds(List<String> musicIds) {
        this.musicIds = musicIds;
    }

    public void addMusic(String musicId) {
        if (!this.musicIds.contains(musicId)) {
            this.musicIds.add(musicId);
        }
    }
}
