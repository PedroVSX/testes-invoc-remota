package com.example.demo.api.graphql;

import com.example.demo.model.Music;
import com.example.demo.model.Playlist;
import com.example.demo.model.User;
import com.example.demo.repository.MockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.Collection;
import java.util.List;

@Controller
public class StreamingGraphQLController {

    private final MockRepository repository;

    @Autowired
    public StreamingGraphQLController(MockRepository repository) {
        this.repository = repository;
    }

    // Q1: Listar todos os usuários
    @QueryMapping
    public Collection<User> listAllUsers() {
        return repository.searchAllUsers();
    }

    // Q2: Listar todas as músicas
    @QueryMapping
    public Collection<Music> listAllMusics() {
        return repository.searchAllMusics();
    }

    // Q3: Listar playlists de um usuário
    @QueryMapping
    public List<Playlist> listPlaylistsByUser(@Argument String userId) {
        return repository.searchPlaylistsByUser(userId);
    }

    // Q4: Listar músicas de uma playlist
    @QueryMapping
    public List<Music> listMusicsFromPlaylist(@Argument String playlistId) {
        return repository.searchMusicsFromPlaylist(playlistId);
    }

    // Q5: Listar playlists que contêm uma música
    @QueryMapping
    public List<Playlist> listPlaylistsByMusic(@Argument String musicId) {
        return repository.searchPlaylistsByMusic(musicId);
    }

}
