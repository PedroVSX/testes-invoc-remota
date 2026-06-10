package analise.java.api.soap.model;

import analise.java.model.Musica;
import jakarta.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "get_musicas_playlistResponse", namespace = "music.soap.service")
public class GetMusicasByPlaylistResponse {
    @XmlElement(name = "musicas")
    public List<Musica> musicas;
    public GetMusicasByPlaylistResponse() {}
}
