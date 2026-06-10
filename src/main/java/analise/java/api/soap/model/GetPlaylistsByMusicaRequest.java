package analise.java.api.soap.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "get_playlists_com_musica", namespace = "music.soap.service")
public class GetPlaylistsByMusicaRequest {
    @XmlElement(name = "musica_id")
    private int musicaId;
    public GetPlaylistsByMusicaRequest() {}
    public int getMusicaId() { return musicaId; }
    public void setMusicaId(int musicaId) { this.musicaId = musicaId; }
}
