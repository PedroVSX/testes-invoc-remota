package analise.java.api.soap;

import analise.java.api.soap.model.*;
import analise.java.repository.DataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class SoapEndpoint {
    private static final Logger logger = LoggerFactory.getLogger(SoapEndpoint.class);
    private static final String NAMESPACE_URI = "music.soap.service";

    private final DataRepository dataRepository;

    public SoapEndpoint(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "get_usuarios")
    @ResponsePayload
    public GetUsuariosResponse get_usuarios(@RequestPayload GetUsuariosRequest request) {
        logger.info("SOAP: get_usuarios");
        GetUsuariosResponse response = new GetUsuariosResponse();
        response.usuarios = dataRepository.getAllUsuarios();
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "get_musicas")
    @ResponsePayload
    public GetMusicasResponse get_musicas(@RequestPayload GetMusicasRequest request) {
        logger.info("SOAP: get_musicas");
        GetMusicasResponse response = new GetMusicasResponse();
        response.musicas = dataRepository.getAllMusicas();
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "get_playlists_usuario")
    @ResponsePayload
    public GetPlaylistsByUsuarioResponse get_playlists_usuario(@RequestPayload GetPlaylistsByUsuarioRequest request) {
        logger.info("SOAP: get_playlists_usuario (uid={})", request.getUsuarioId());
        GetPlaylistsByUsuarioResponse response = new GetPlaylistsByUsuarioResponse();
        response.playlists = dataRepository.getPlaylistsByUsuarioId(request.getUsuarioId());
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "get_musicas_playlist")
    @ResponsePayload
    public GetMusicasByPlaylistResponse get_musicas_playlist(@RequestPayload GetMusicasByPlaylistRequest request) {
        logger.info("SOAP: get_musicas_playlist (pid={})", request.getPlaylistId());
        GetMusicasByPlaylistResponse response = new GetMusicasByPlaylistResponse();
        response.musicas = dataRepository.getMusicasByPlaylistId(request.getPlaylistId());
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "get_playlists_com_musica")
    @ResponsePayload
    public GetPlaylistsByMusicaResponse get_playlists_com_musica(@RequestPayload GetPlaylistsByMusicaRequest request) {
        logger.info("SOAP: get_playlists_com_musica (mid={})", request.getMusicaId());
        GetPlaylistsByMusicaResponse response = new GetPlaylistsByMusicaResponse();
        response.playlists = dataRepository.getPlaylistsByMusicaId(request.getMusicaId());
        return response;
    }
}
