package analise.java.api.soap.model;

import analise.java.model.Usuario;
import jakarta.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "get_usuariosResponse", namespace = "music.soap.service")
public class GetUsuariosResponse {
    @XmlElement(name = "usuarios")
    public List<Usuario> usuarios;
    public GetUsuariosResponse() {}
}
