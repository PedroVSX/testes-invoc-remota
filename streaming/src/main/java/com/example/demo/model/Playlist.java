package com.example.demo.model;

import java.util.List;

public class Playlist {
    private String id;
    private String name;
    private String ownerId;
    private String description;
    private List<String> musicsIds;

    public Playlist() {}
    public Playlist(String id, String name, String ownerId, String description, List<String> musicsIds) {
        this.id = id;
        this.name = name;
        this.ownerId = ownerId;
        this.description = description;
        this.musicsIds = musicsIds;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<String> getMusicsIds() { return musicsIds; }
    public void setMusicsIds(List<String> musicsIds) { this.musicsIds = musicsIds; }
}