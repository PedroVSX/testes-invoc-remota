from ariadne import ObjectType, QueryType, make_executable_schema
from ariadne.asgi import GraphQL
import uvicorn
import json

def carregar_dados():
    with open('t_6/dataset.json', 'r', encoding='utf-8') as f:
        return json.load(f)

data = carregar_dados()

# 1. Schema
type_defs = """
    type Usuario { id: Int, nome: String }
    type Musica { id: Int, titulo: String, artista: String }
    type Playlist { id: Int, nome: String, usuario: Usuario, musicas: [Musica] }

    type Query {
        usuarios: [Usuario]
        musicas: [Musica]
        playlistsPorUsuario(usuario_id: Int!): [Playlist]
        musicasPorPlaylist(playlist_id: Int!): [Musica]
        playlistsPorMusica(musica_id: Int!): [Playlist]
    }
"""

query = QueryType()
playlist = ObjectType("Playlist")

# 2. Resolvers de Query
@query.field("usuarios")
def resolve_usuarios(*_): return data["usuarios"]

@query.field("musicas")
def resolve_musicas(*_): return data["musicas"]

@query.field("playlistsPorUsuario")
def resolve_playlists_usuario(_, info, usuario_id):
    return [p for p in data["playlists"] if p["usuario_id"] == usuario_id]

@query.field("musicasPorPlaylist")
def resolve_musicas_playlist(_, info, playlist_id):
    p = next((p for p in data["playlists"] if p["id"] == playlist_id), None)
    return [m for m in data["musicas"] if m["id"] in p.get("musicas", [])] if p else []

@query.field("playlistsPorMusica")
def resolve_playlists_musica(_, info, musica_id):
    return [p for p in data["playlists"] if musica_id in p.get("musicas", [])]

@playlist.field("usuario")
def resolve_playlist_usuario(obj, info):
    return next((u for u in data["usuarios"] if u["id"] == obj["usuario_id"]), None)

@playlist.field("musicas")
def resolve_playlist_musicas(obj, info):
    return [m for m in data["musicas"] if m["id"] in obj.get("musicas", [])]

schema = make_executable_schema(type_defs, [query, playlist])
app = GraphQL(schema, debug=True)

if __name__ == "__main__":
    port = 5001
    print("Servidor Python GraphQL rodando")
    uvicorn.run(app, host="127.0.0.1", port=5001)