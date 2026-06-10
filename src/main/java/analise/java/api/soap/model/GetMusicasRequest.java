package analise.java.api.soap.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "get_musicas", namespace = "music.soap.service")
public class GetMusicasRequest {
    public GetMusicasRequest() {}
}
