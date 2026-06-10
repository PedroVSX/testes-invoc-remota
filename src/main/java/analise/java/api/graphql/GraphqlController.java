package analise.java.api.graphql;

import analise.java.model.Musica;
import analise.java.model.PlaylistEntity;
import analise.java.model.Usuario;
import analise.java.repository.DataRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class GraphqlController {
    private static final Logger logger = LoggerFactory.getLogger(GraphqlController.class);

    private final DataRepository dataRepository;

    public GraphqlController(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    @QueryMapping
    public List<Usuario> usuarios() {
        logger.info("GraphQL: usuarios");
        return dataRepository.getAllUsuarios();
    }

    @QueryMapping
    public List<Musica> musicas() {
        logger.info("GraphQL: musicas");
        return dataRepository.getAllMusicas();
    }

    @QueryMapping
    public List<PlaylistEntity> playlistsPorUsuario(@Argument Integer usuario_id) {
        logger.info("GraphQL: playlistsPorUsuario id={}", usuario_id);
        return dataRepository.getPlaylistsByUsuarioId(usuario_id);
    }

    @QueryMapping
    public List<Musica> musicasPorPlaylist(@Argument Integer playlist_id) {
        logger.info("GraphQL: musicasPorPlaylist id={}", playlist_id);
        return dataRepository.getMusicasByPlaylistId(playlist_id);
    }

    @QueryMapping
    public List<PlaylistEntity> playlistsPorMusica(@Argument Integer musica_id) {
        logger.info("GraphQL: playlistsPorMusica id={}", musica_id);
        return dataRepository.getPlaylistsByMusicaId(musica_id);
    }

    // Resolvers para os campos aninhados do tipo Playlist
    @SchemaMapping(typeName = "Playlist", field = "usuario")
    public Usuario getUsuario(PlaylistEntity playlist) {
        return dataRepository.getUsuarioById(playlist.getUsuarioId());
    }

    @SchemaMapping(typeName = "Playlist", field = "musicas")
    public List<Musica> getMusicas(PlaylistEntity playlist) {
        return dataRepository.getMusicasByPlaylistId(playlist.getId());
    }
}
