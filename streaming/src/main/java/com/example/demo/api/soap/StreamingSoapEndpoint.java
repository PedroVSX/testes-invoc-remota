package com.example.demo.api.soap;

import com.example.demo.repository.MockRepository;
import com.example.demo.soap.*; // Importa as classes XML geradas pelo JAXB
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class StreamingSoapEndpoint {

    private static final String NAMESPACE_URI = "http://example.com/demo/soap";
    private final MockRepository repository;

    @Autowired
    public StreamingSoapEndpoint(MockRepository repository) {
        this.repository = repository;
    }

    // Q1: Listar todos os usuários
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "listAllUsersRequest")
    @ResponsePayload
    public ListAllUsersResponse listAllUsers() {
        ListAllUsersResponse response = new ListAllUsersResponse();
        repository.searchAllUsers().forEach(u -> {
            SoapUser su = new SoapUser();
            su.setId(u.getId());
            su.setName(u.getName());
            su.setAge(u.getAge());
            response.getUsers().add(su);
        });
        return response;
    }

    // Q2: Listar todas as músicas
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "listAllMusicsRequest")
    @ResponsePayload
    public ListAllMusicsResponse listAllMusics() {
        ListAllMusicsResponse response = new ListAllMusicsResponse();
        repository.searchAllMusics().forEach(m -> {
            SoapMusic sm = new SoapMusic();
            sm.setId(m.getId());
            sm.setName(m.getName());
            sm.setArtist(m.getArtist());
            response.getMusics().add(sm);
        });
        return response;
    }

    // Q3: Listar playlists de um usuário
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "listPlaylistsByUserRequest")
    @ResponsePayload
    public ListPlaylistsByUserResponse listPlaylistsByUser(@RequestPayload ListPlaylistsByUserRequest request) {
        ListPlaylistsByUserResponse response = new ListPlaylistsByUserResponse();
        repository.searchPlaylistsByUser(request.getUserId()).forEach(p -> {
            SoapPlaylist sp = new SoapPlaylist();
            sp.setId(p.getId());
            sp.setName(p.getName());
            sp.getMusicsIds().addAll(p.getMusicsIds());
            response.getPlaylists().add(sp);
        });
        return response;
    }

    // Q4: Listar músicas de uma playlist
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "listMusicsFromPlaylistRequest")
    @ResponsePayload
    public ListMusicsFromPlaylistResponse listMusicsFromPlaylist(@RequestPayload ListMusicsFromPlaylistRequest request) {
        ListMusicsFromPlaylistResponse response = new ListMusicsFromPlaylistResponse();
        repository.searchMusicsFromPlaylist(request.getPlaylistId()).forEach(m -> {
            SoapMusic sm = new SoapMusic();
            sm.setId(m.getId());
            sm.setName(m.getName());
            sm.setArtist(m.getArtist());
            response.getMusics().add(sm);
        });
        return response;
    }

    // Q5: Listar playlists que contêm uma música
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "listPlaylistsByMusicRequest")
    @ResponsePayload
    public ListPlaylistsByMusicResponse listPlaylistsByMusic(@RequestPayload ListPlaylistsByMusicRequest request) {
        ListPlaylistsByMusicResponse response = new ListPlaylistsByMusicResponse();
        repository.searchPlaylistsByMusic(request.getMusicId()).forEach(p -> {
            SoapPlaylist sp = new SoapPlaylist();
            sp.setId(p.getId());
            sp.setName(p.getName());
            sp.getMusicsIds().addAll(p.getMusicsIds());
            response.getPlaylists().add(sp);
        });
        return response;
    }
}