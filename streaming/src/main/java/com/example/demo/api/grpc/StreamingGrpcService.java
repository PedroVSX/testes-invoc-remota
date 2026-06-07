package com.example.demo.api.grpc;

import com.example.demo.grpc.*;
import com.example.demo.model.Music;
import com.example.demo.model.Playlist;
import com.example.demo.model.User;
import com.example.demo.repository.MockRepository;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@GrpcService
public class StreamingGrpcService extends StreamingServiceGrpc.StreamingServiceImplBase {

    private final MockRepository repository;

    @Autowired
    public StreamingGrpcService(MockRepository repository) {
        this.repository = repository;
    }

    // Q1: Listar todos os usuários
    @Override
    public void listAllUsers(EmptyRequest request, StreamObserver<UserListResponse> responseObserver) {
        List<UserResponse> grpcUsers = repository.searchAllUsers().stream()
                .map(u -> UserResponse.newBuilder().setId(u.getId()).setName(u.getName()).setAge(u.getAge()).build())
                .collect(Collectors.toList());

        UserListResponse response = UserListResponse.newBuilder().addAllUsers(grpcUsers).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    // Q2: Listar todas as músicas
    @Override
    public void listAllMusics(EmptyRequest request, StreamObserver<MusicListResponse> responseObserver) {
        List<MusicResponse> grpcMusics = repository.searchAllMusics().stream()
                .map(m -> MusicResponse.newBuilder().setId(m.getId()).setName(m.getName()).setArtist(m.getArtist()).build())
                .collect(Collectors.toList());

        MusicListResponse response = MusicListResponse.newBuilder().addAllMusics(grpcMusics).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    // Q3: Listar playlists de um usuário
    @Override
    public void listPlaylistsByUser(UserRequest request, StreamObserver<PlaylistListResponse> responseObserver) {
        List<PlaylistResponse> grpcPlaylists = repository.searchPlaylistsByUser(request.getUserId()).stream()
                .map(p -> PlaylistResponse.newBuilder()
                        .setId(p.getId())
                        .setName(p.getName()) // Alterado para getName()
                        .addAllMusicsIds(p.getMusicsIds()) // Alterado para getMusicsIds()
                        .build())
                .map(p -> (PlaylistResponse) p) // Garante a coerência de tipo para o Java
                .collect(Collectors.toList());

        PlaylistListResponse response = PlaylistListResponse.newBuilder().addAllPlaylists(grpcPlaylists).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    // Q4: Listar músicas de uma playlist
    @Override
    public void listMusicsFromPlaylist(PlaylistRequest request, StreamObserver<MusicListResponse> responseObserver) {
        List<MusicResponse> grpcMusics = repository.searchMusicsFromPlaylist(request.getPlaylistId()).stream()
                .map(m -> MusicResponse.newBuilder().setId(m.getId()).setName(m.getName()).setArtist(m.getArtist()).build())
                .collect(Collectors.toList());

        MusicListResponse response = MusicListResponse.newBuilder().addAllMusics(grpcMusics).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    // Q5: Listar playlists que contêm uma música
    @Override
    public void listPlaylistsByMusic(MusicRequest request, StreamObserver<PlaylistListResponse> responseObserver) {
        List<PlaylistResponse> grpcPlaylists = repository.searchPlaylistsByMusic(request.getMusicId()).stream()
                .map(p -> PlaylistResponse.newBuilder()
                        .setId(p.getId())
                        .setName(p.getName()) // Alterado para getName()
                        .addAllMusicsIds(p.getMusicsIds()) // Alterado para getMusicsIds()
                        .build())
                .map(p -> (PlaylistResponse) p) // Garante a coerência de tipo para o Java
                .collect(Collectors.toList());

        PlaylistListResponse response = PlaylistListResponse.newBuilder().addAllPlaylists(grpcPlaylists).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}