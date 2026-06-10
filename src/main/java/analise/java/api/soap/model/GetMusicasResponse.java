package analise.java.api.soap.model;

import analise.java.model.Musica;
import jakarta.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "get_musicasResponse", namespace = "music.soap.service")
public class GetMusicasResponse {
    @XmlElement(name = "musicas")
    public List<Musica> musicas;
    public GetMusicasResponse() {}
}
