package analise.java.api.soap.model;

import analise.java.model.PlaylistEntity;
import jakarta.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "get_playlists_usuarioResponse", namespace = "music.soap.service")
public class GetPlaylistsByUsuarioResponse {
    @XmlElement(name = "playlists")
    public List<PlaylistEntity> playlists;
    public GetPlaylistsByUsuarioResponse() {}
}
