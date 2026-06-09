package com.example.demo.api.grpc;

import com.example.demo.grpc.*;
import com.example.demo.grpc.StreamingServiceGrpc.StreamingServiceImplBase;
import com.example.demo.model.Music;
import com.example.demo.model.Playlist;
import com.example.demo.model.User;
import com.example.demo.repository.MockRepository;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.List;

@GrpcService
public class StreamingGrpcService extends StreamingServiceImplBase {

    @Autowired
    private MockRepository repository;

    // Q1: Listar os dados de todos os usuários
    @Override
    public void listAllUsers(EmptyRequest request, StreamObserver<UserListResponse> responseObserver) {
        Collection<User> domainUsers = repository.searchAllUsers();
        UserListResponse.Builder responseBuilder = UserListResponse.newBuilder();

        for (User user : domainUsers) {
            UserResponse grpcUser = UserResponse.newBuilder()
                    .setId(user.getId())
                    .setName(user.getName())
                    .setEmail(user.getEmail())
                    .setCountry(user.getCountry())
                    .setAge(user.getAge())
                    .build();
            responseBuilder.addUsers(grpcUser);
        }

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    // Q2: Listar os dados de todas as músicas
    @Override
    public void listAllMusics(EmptyRequest request, StreamObserver<MusicListResponse> responseObserver) {
        Collection<Music> domainMusics = repository.searchAllMusics();
        MusicListResponse.Builder responseBuilder = MusicListResponse.newBuilder();

        for (Music music : domainMusics) {
            MusicResponse grpcMusic = MusicResponse.newBuilder()
                    .setId(music.getId())
                    .setName(music.getName())
                    .setArtist(music.getArtist())
                    .setGenre(music.getGenre())
                    .setDurationSeconds(music.getDurationSeconds())
                    .build();
            responseBuilder.addMusics(grpcMusic);
        }

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    // Q3: Listar as playlists de um determinado usuário
    @Override
    public void listPlaylistsByUser(UserRequest request, StreamObserver<PlaylistListResponse> responseObserver) {
        List<Playlist> domainPlaylists = repository.searchPlaylistsByUser(request.getUserId());
        PlaylistListResponse.Builder responseBuilder = PlaylistListResponse.newBuilder();

        for (Playlist playlist : domainPlaylists) {
            PlaylistResponse grpcPlaylist = PlaylistResponse.newBuilder()
                    .setId(playlist.getId())
                    .setName(playlist.getName())
                    .setOwnerId(playlist.getOwnerId())
                    .setDescription(playlist.getDescription())
                    .addAllMusicsIds(playlist.getMusicsIds())
                    .build();
            responseBuilder.addPlaylists(grpcPlaylist);
        }

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    // Q4: Listar todas as músicas de uma determinada playlist
    @Override
    public void listMusicsFromPlaylist(PlaylistRequest request, StreamObserver<MusicListResponse> responseObserver) {
        List<Music> domainMusics = repository.searchMusicsFromPlaylist(request.getPlaylistId());
        MusicListResponse.Builder responseBuilder = MusicListResponse.newBuilder();

        for (Music music : domainMusics) {
            MusicResponse grpcMusic = MusicResponse.newBuilder()
                    .setId(music.getId())
                    .setName(music.getName())
                    .setArtist(music.getArtist())
                    .setGenre(music.getGenre())
                    .setDurationSeconds(music.getDurationSeconds())
                    .build();
            responseBuilder.addMusics(grpcMusic);
        }

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    // Q5: Listar todas as playlists que contêm uma determinada música
    @Override
    public void listPlaylistsByMusic(MusicRequest request, StreamObserver<PlaylistListResponse> responseObserver) {
        List<Playlist> domainPlaylists = repository.searchPlaylistsByMusic(request.getMusicId());
        PlaylistListResponse.Builder responseBuilder = PlaylistListResponse.newBuilder();

        for (Playlist playlist : domainPlaylists) {
            PlaylistResponse grpcPlaylist = PlaylistResponse.newBuilder()
                    .setId(playlist.getId())
                    .setName(playlist.getName())
                    .setOwnerId(playlist.getOwnerId())
                    .setDescription(playlist.getDescription())
                    .addAllMusicsIds(playlist.getMusicsIds())
                    .build();
            responseBuilder.addPlaylists(grpcPlaylist);
        }

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }
}