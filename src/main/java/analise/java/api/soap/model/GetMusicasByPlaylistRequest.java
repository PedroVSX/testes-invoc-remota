package analise.java.api.soap.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "get_musicas_playlist", namespace = "music.soap.service")
public class GetMusicasByPlaylistRequest {
    @XmlElement(name = "playlist_id")
    private int playlistId;
    public GetMusicasByPlaylistRequest() {}
    public int getPlaylistId() { return playlistId; }
    public void setPlaylistId(int playlistId) { this.playlistId = playlistId; }
}
