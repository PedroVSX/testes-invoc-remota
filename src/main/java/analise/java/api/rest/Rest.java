package analise.java.api.rest;

import analise.java.model.Musica;
import analise.java.model.PlaylistEntity;
import analise.java.model.Usuario;
import analise.java.repository.DataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/")
public class Rest {
    private static final Logger logger = LoggerFactory.getLogger(Rest.class);

    private final DataRepository dataRepository;

    public Rest(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    /**
     * Lista todos os usuários
     * GET /api/usuarios
     */
    @GetMapping("/usuarios")
    public ResponseEntity<List<Usuario>> getAllUsuarios() {
        logger.info("REST: getAllUsuarios");
        return ResponseEntity.ok(dataRepository.getAllUsuarios());
    }

    /**
     * Lista todas as músicas
     * GET /api/musicas
     */
    @GetMapping("/musicas")
    public ResponseEntity<List<Musica>> getAllMusicas() {
        logger.info("REST: getAllMusicas");
        return ResponseEntity.ok(dataRepository.getAllMusicas());
    }

    /**
     * Lista todas as playlists de um usuário
     * GET /api/usuarios/{usuarioId}/playlists
     */
    @GetMapping("/usuarios/{usuarioId}/playlists")
    public ResponseEntity<List<PlaylistEntity>> getPlaylistsByUsuario(@PathVariable Integer usuarioId) {
        logger.info("REST: getPlaylistsByUsuario id={}", usuarioId);
        Usuario usuario = dataRepository.getUsuarioById(usuarioId);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dataRepository.getPlaylistsByUsuarioId(usuarioId));
    }

    /**
     * Lista as músicas de uma playlist
     * GET /api/playlists/{playlistId}/musicas
     */
    @GetMapping("/playlists/{playlistId}/musicas")
    public ResponseEntity<List<Musica>> getMusicasByPlaylist(@PathVariable Integer playlistId) {
        logger.info("REST: getMusicasByPlaylist id={}", playlistId);
        List<Musica> musicas = dataRepository.getMusicasByPlaylistId(playlistId);
        if (musicas.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(musicas);
    }

    /**
     * Lista todas as playlists que possuem uma música específica
     * GET /api/musicas/{musicaId}/playlists
     */
    @GetMapping("/musicas/{musicaId}/playlists")
    public ResponseEntity<List<PlaylistEntity>> getPlaylistsByMusica(@PathVariable Integer musicaId) {
        logger.info("REST: getPlaylistsByMusica id={}", musicaId);
        Musica musica = dataRepository.getMusicaById(musicaId);
        if (musica == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dataRepository.getPlaylistsByMusicaId(musicaId));
    }
}