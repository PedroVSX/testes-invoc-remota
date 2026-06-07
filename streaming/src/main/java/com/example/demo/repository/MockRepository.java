package com.example.demo.repository;

import com.example.demo.model.Music;
import com.example.demo.model.Playlist;
import com.example.demo.model.User;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class MockRepository {

    private final Map<String, User> users = new ConcurrentHashMap<>();
    private final Map<String, Music> musics = new ConcurrentHashMap<>();
    private final Map<String, Playlist> playlists = new ConcurrentHashMap<>();

    private final Map<String, List<Playlist>> playlistsPerUser = new ConcurrentHashMap<>();
    private final Map<String, List<Playlist>> playlistsPerMusic = new ConcurrentHashMap<>();

    @PostConstruct
    public void carregarDadosIniciais() {
        System.out.println("=== Populado banco de dados em memória ===");

        // 1. Gerar Músicas
        for (int i = 1; i <= 200; i++) {
            String id = "m" + i;
            Music music = new Music(id, "Music " + i, "Artist " + ((i % 10) + 1));
            musics.put(id, music);
        }

        // 2. Gerar Usuários
        for (int i = 1; i <= 50; i++) {
            String id = "u" + i;
            User user = new User(id, "Usuário " + i, 20 + (i % 30));
            users.put(id, user);

            // Inicializa a lista de playlists deste usuário no índice
            playlistsPerUser.put(id, new ArrayList<>());
        }

        // 3. Gerar Playlists e fazer os vínculos (Índices)
        int playlistCount = 1;
        for (String userId : users.keySet()) {
            // Cada usuário terá 2 playlists criadas automaticamente
            for (int p = 1; p <= 2; p++) {
                String playlistId = "p" + playlistCount;
                Playlist playlist = new Playlist(playlistId, "Playlist " + p + " of " + userId);

                // Adiciona algumas músicas aleatórias nesta playlist (ex: 5 músicas)
                for (int m = 1; m <= 5; m++) {
                    int musicRandomId = (playlistCount * m) % 200 + 1;
                    String musicId = "m" + musicRandomId;

                    playlist.addMusic(musicId);

                    // Alimenta o índice reverso: Quais playlists contêm a música X?
                    playlistsPerMusic.computeIfAbsent(musicId, k -> Collections.synchronizedList(new ArrayList<>()))
                            .add(playlist);
                }

                playlists.put(playlistId, playlist);

                // Alimenta o índice: Quais playlists pertencem ao usuário Y?
                playlistsPerUser.get(userId).add(playlist);

                playlistCount++;
            }
        }

        System.out.println("=== Massa de dados carregada com sucesso! ===");
    }

    // Q1: Listar os dados de todos os usuários do serviço
    public Collection<User> searchAllUsers() {
        return users.values();
    }

    // Q2: Listar os dados de todas as músicas mantidas pelo serviço
    public Collection<Music> searchAllMusics() {
        return musics.values();
    }

    // Buscar música por ID (Auxiliar para converter IDs em objetos completos)
    public Music searchMusicById(String id) {
        return musics.get(id);
    }

    // Q3: Listar os dados de todas as playlists de um determinado usuário
    public List<Playlist> searchPlaylistsByUser(String userId) {
        return playlistsPerUser.getOrDefault(userId, Collections.emptyList());
    }

    // Q4: Listar os dados de todas as músicas de uma determinada playlist
    public List<Music> searchMusicsFromPlaylist(String playlistId) {
        Playlist playlist = playlists.get(playlistId);
        if (playlist == null) return Collections.emptyList();

        List<Music> result = new ArrayList<>();
        for (String musicId : playlist.getMusicsIds()) {
            Music m = musics.get(musicId);
            if (m != null) {
                result.add(m);
            }
        }
        return result;
    }

    // Q5: Listar os dados de todas as playlists que contêm uma determinada música
    public List<Playlist> searchPlaylistsByMusic(String musicId) {
        return playlistsPerMusic.getOrDefault(musicId, Collections.emptyList());
    }
}
