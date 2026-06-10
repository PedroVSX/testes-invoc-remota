package analise.java.repository;

import analise.java.model.Dataset;
import analise.java.model.Musica;
import analise.java.model.PlaylistEntity;
import analise.java.model.Usuario;
import tools.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class DataRepository {

    private Dataset dataset;
    private Map<Integer, Usuario> usuarioMap;
    private Map<Integer, Musica> musicaMap;

    @PostConstruct
    public void init() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        // Tenta carregar do classpath primeiro, depois do root do projeto
        try {
            InputStream is = new ClassPathResource("dataset.json").getInputStream();
            dataset = mapper.readValue(is, Dataset.class);
        } catch (IOException e) {
            // Fallback: tenta carregar do root do projeto
            File file = new File("dataset.json");
            dataset = mapper.readValue(file, Dataset.class);
        }

        // Criar maps para acesso rápido
        usuarioMap = dataset.getUsuarios().stream()
                .collect(Collectors.toMap(Usuario::getId, u -> u));

        musicaMap = dataset.getMusicas().stream()
                .collect(Collectors.toMap(Musica::getId, m -> m));
    }

    public List<Usuario> getAllUsuarios() {
        return dataset.getUsuarios();
    }

    public List<Musica> getAllMusicas() {
        return dataset.getMusicas();
    }

    public List<PlaylistEntity> getPlaylistsByUsuarioId(Integer usuarioId) {
        return dataset.getPlaylists().stream()
                .filter(p -> p.getUsuarioId().equals(usuarioId))
                .collect(Collectors.toList());
    }

    public List<Musica> getMusicasByPlaylistId(Integer playlistId) {
        return dataset.getPlaylists().stream()
                .filter(p -> p.getId().equals(playlistId))
                .findFirst()
                .map(playlist -> playlist.getMusicas().stream()
                        .map(musicaMap::get)
                        .filter(m -> m != null)
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    public List<PlaylistEntity> getPlaylistsByMusicaId(Integer musicaId) {
        return dataset.getPlaylists().stream()
                .filter(p -> p.getMusicas().contains(musicaId))
                .collect(Collectors.toList());
    }

    public Usuario getUsuarioById(Integer id) {
        return usuarioMap.get(id);
    }

    public Musica getMusicaById(Integer id) {
        return musicaMap.get(id);
    }
}