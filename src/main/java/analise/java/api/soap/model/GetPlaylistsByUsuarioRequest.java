package analise.java.api.soap.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "get_playlists_usuario", namespace = "music.soap.service")
public class GetPlaylistsByUsuarioRequest {
    @XmlElement(name = "usuario_id")
    private int usuarioId;
    public GetPlaylistsByUsuarioRequest() {}
    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }
}
