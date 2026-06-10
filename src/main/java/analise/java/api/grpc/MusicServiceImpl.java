package analise.java.api.grpc;

import analise.java.api.grpc.gen.*;
import net.devh.boot.grpc.server.service.GrpcService;
import analise.java.repository.DataRepository;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.stream.Collectors;

@GrpcService
public class MusicServiceImpl extends analise.java.api.grpc.gen.MusicServiceGrpc.MusicServiceImplBase {
    private static final Logger logger = LoggerFactory.getLogger(MusicServiceImpl.class);

    private final DataRepository dataRepository;

    public MusicServiceImpl(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    @Override
    public void getUsuarios(Empty request, StreamObserver<UsuariosResponse> responseObserver) {
        logger.info("gRPC: getUsuarios");
        var usuarios = dataRepository.getAllUsuarios().stream()
                .map(u -> Usuario.newBuilder()
                        .setId(u.getId())
                        .setNome(u.getNome())
                        .setDataNascimento(u.getDataNascimento() != null ? u.getDataNascimento() : "")
                        .setSexo(u.getSexo() != null ? u.getSexo() : "")
                        .setCidadeNascimento(u.getCidadeNascimento() != null ? u.getCidadeNascimento() : "")
                        .build())
                .collect(Collectors.toList());

        responseObserver.onNext(UsuariosResponse.newBuilder().addAllUsuarios(usuarios).build());
        responseObserver.onCompleted();
    }

    @Override
    public void getMusicas(Empty request, StreamObserver<MusicasResponse> responseObserver) {
        logger.info("gRPC: getMusicas");
        var musicas = dataRepository.getAllMusicas().stream()
                .map(m -> Musica.newBuilder()
                        .setId(m.getId())
                        .setTitulo(m.getTitulo())
                        .setArtista(m.getArtista())
                        .setGenero(m.getGenero() != null ? m.getGenero() : "")
                        .setDuracao(m.getDuracao() != null ? m.getDuracao() : 0)
                        .build())
                .collect(Collectors.toList());

        responseObserver.onNext(MusicasResponse.newBuilder().addAllMusicas(musicas).build());
        responseObserver.onCompleted();
    }

    @Override
    public void getPlaylistsUsuario(UsuarioRequest request, StreamObserver<PlaylistsResponse> responseObserver) {
        logger.info("gRPC: getPlaylistsUsuario (uid={})", request.getUsuarioId());
        var playlists = dataRepository.getPlaylistsByUsuarioId(request.getUsuarioId()).stream()
                .map(p -> Playlist.newBuilder()
                        .setId(p.getId())
                        .setUsuarioId(p.getUsuarioId())
                        .setNome(p.getNome())
                        .addAllMusicas(p.getMusicas())
                        .build())
                .collect(Collectors.toList());

        responseObserver.onNext(PlaylistsResponse.newBuilder().addAllPlaylists(playlists).build());
        responseObserver.onCompleted();
    }

    @Override
    public void getMusicasPlaylist(PlaylistRequest request, StreamObserver<MusicasResponse> responseObserver) {
        logger.info("gRPC: getMusicasPlaylist (pid={})", request.getPlaylistId());
        var musicas = dataRepository.getMusicasByPlaylistId(request.getPlaylistId()).stream()
                .map(m -> Musica.newBuilder()
                        .setId(m.getId())
                        .setTitulo(m.getTitulo())
                        .setArtista(m.getArtista())
                        .setGenero(m.getGenero() != null ? m.getGenero() : "")
                        .setDuracao(m.getDuracao() != null ? m.getDuracao() : 0)
                        .build())
                .collect(Collectors.toList());

        responseObserver.onNext(MusicasResponse.newBuilder().addAllMusicas(musicas).build());
        responseObserver.onCompleted();
    }

    @Override
    public void getPlaylistsComMusica(MusicaRequest request, StreamObserver<PlaylistsResponse> responseObserver) {
        logger.info("gRPC: getPlaylistsComMusica (mid={})", request.getMusicaId());
        var playlists = dataRepository.getPlaylistsByMusicaId(request.getMusicaId()).stream()
                .map(p -> Playlist.newBuilder()
                        .setId(p.getId())
                        .setUsuarioId(p.getUsuarioId())
                        .setNome(p.getNome())
                        .addAllMusicas(p.getMusicas())
                        .build())
                .collect(Collectors.toList());

        responseObserver.onNext(PlaylistsResponse.newBuilder().addAllPlaylists(playlists).build());
        responseObserver.onCompleted();
    }
}
