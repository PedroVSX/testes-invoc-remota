package com.example.demo.api.rest;

import com.example.demo.model.Music;
import com.example.demo.model.Playlist;
import com.example.demo.model.User;
import com.example.demo.repository.MockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api")
public class StreamingRestController {

    private final MockRepository repository;

    @Autowired
    public StreamingRestController(MockRepository repository) {
        this.repository = repository;
    }

    // Q1: Listar os dados de todos os usuários do serviço
    @GetMapping("/users")
    public ResponseEntity<Collection<User>> listAllUsers() {
        return ResponseEntity.ok(repository.searchAllUsers());
    }

    // Q2: Listar os dados de todas as músicas mantidas pelo serviço
    @GetMapping("/musics")
    public ResponseEntity<Collection<Music>> listAllMusics() {
        return ResponseEntity.ok(repository.searchAllMusics());
    }

    // Q3: Listar os dados de todas as playlists de um determinado usuário
    @GetMapping("/users/{userId}/playlists")
    public ResponseEntity<List<Playlist>> listPlaylistsByUser(@PathVariable String userId) {
        return ResponseEntity.ok(repository.searchPlaylistsByUser(userId));
    }

    // Q4: Listar os dados de todas as músicas de uma determinada playlist
    @GetMapping("/playlists/{playlistId}/musics")
    public ResponseEntity<List<Music>> listMusicsFromPlaylist(@PathVariable String playlistId) {
        return ResponseEntity.ok(repository.searchMusicsFromPlaylist(playlistId));
    }

    // Q5: Listar os dados de todas as playlists que contêm uma determinada música
    @GetMapping("/musics/{musicId}/playlists")
    public ResponseEntity<List<Playlist>> listPlaylistsByMusic(@PathVariable String musicId) {
        return ResponseEntity.ok(repository.searchPlaylistsByMusic(musicId));
    }

}
