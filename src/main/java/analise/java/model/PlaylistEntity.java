package analise.java.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "playlist", namespace = "http://analise.java/api/soap")
public class PlaylistEntity {
    private Integer id;

    @JsonProperty("usuario_id")
    private Integer usuarioId;

    private String nome;
    private List<Integer> musicas;

    public PlaylistEntity() {}

    public PlaylistEntity(Integer id, Integer usuarioId, String nome, List<Integer> musicas) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.nome = nome;
        this.musicas = musicas;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<Integer> getMusicas() {
        return musicas;
    }

    public void setMusicas(List<Integer> musicas) {
        this.musicas = musicas;
    }
}