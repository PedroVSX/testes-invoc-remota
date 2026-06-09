package com.example.demo.repository;

import com.example.demo.model.Music;
import com.example.demo.model.Playlist;
import com.example.demo.model.User;
import com.example.demo.model.DataContainer; // Certifique-se de que o import está correto
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class MockRepository {

    // Mantendo a sua estrutura original de Mapas e Índices rápidos
    private final Map<String, User> users = new ConcurrentHashMap<>();
    private final Map<String, Music> musics = new ConcurrentHashMap<>();
    private final Map<String, Playlist> playlists = new ConcurrentHashMap<>();

    private final Map<String, List<Playlist>> playlistsPerUser = new ConcurrentHashMap<>();
    private final Map<String, List<Playlist>> playlistsPerMusic = new ConcurrentHashMap<>();

    @PostConstruct
    public void carregarDadosIniciais() {
        System.out.println("=== Carregando banco de dados em memória a partir do JSON ===");

        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("dados_streaming.json");
            
            if (inputStream == null) {
                System.err.println("❌ Erro: Arquivo dados_streaming.json não encontrado em src/main/resources!");
                return;
            }

            DataContainer container = mapper.readValue(inputStream, DataContainer.class);

            // 1. Popular o Mapa de Músicas
            if (container.getMusics() != null) {
                for (Music music : container.getMusics()) {
                    musics.put(music.getId(), music);
                }
            }

            // 2. Popular o Mapa de Usuários e inicializar o índice de playlists por usuário
            if (container.getUsers() != null) {
                for (User user : container.getUsers()) {
                    users.put(user.getId(), user);
                    playlistsPerUser.put(user.getId(), Collections.synchronizedList(new ArrayList<>()));
                }
            }

            // 3. Popular o Mapa de Playlists e construir os índices reverso/vínculos
            if (container.getPlaylists() != null) {
                for (Playlist playlist : container.getPlaylists()) {
                    playlists.put(playlist.getId(), playlist);

                    // Alimenta o índice: Quais playlists pertencem ao usuário Y?
                    if (playlist.getOwnerId() != null && playlistsPerUser.containsKey(playlist.getOwnerId())) {
                        playlistsPerUser.get(playlist.getOwnerId()).add(playlist);
                    }

                    // Alimenta o índice reverso: Quais playlists contêm a música X?
                    if (playlist.getMusicsIds() != null) {
                        for (String musicId : playlist.getMusicsIds()) {
                            playlistsPerMusic.computeIfAbsent(musicId, k -> Collections.synchronizedList(new ArrayList<>()))
                                    .add(playlist);
                        }
                    }
                }
            }

            System.out.println("=== Massa de dados do JSON carregada com sucesso! ===");
            System.out.println("👉 Usuários mapeados: " + users.size());
            System.out.println("👉 Músicas mapeadas: " + musics.size());
            System.out.println("👉 Playlists mapeadas: " + playlists.size());

        } catch (Exception e) {
            System.err.println("❌ Erro crítico ao carregar ou parsear o JSON: " + e.getMessage());
            e.printStackTrace();
        }
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