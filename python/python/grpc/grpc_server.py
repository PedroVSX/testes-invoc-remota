import grpc
from concurrent import futures
import json
import music_service_pb2
import music_service_pb2_grpc
from grpc_reflection.v1alpha import reflection

def carregar_dados():
    with open('t_6/dataset.json', 'r', encoding='utf-8') as f:
        return json.load(f)

data = carregar_dados()

class MusicServiceServicer(music_service_pb2_grpc.MusicServiceServicer):

    def GetUsuarios(self, request, context):
        usuarios = [music_service_pb2.Usuario(id=u["id"], nome=u["nome"]) for u in data["usuarios"]]
        return music_service_pb2.UsuariosResponse(usuarios=usuarios)

    def GetMusicas(self, request, context):
        musicas = [music_service_pb2.Musica(id=m["id"], titulo=m["titulo"], artista=m["artista"]) for m in data["musicas"]]
        return music_service_pb2.MusicasResponse(musicas=musicas)

    def GetPlaylistsUsuario(self, request, context):
        playlists = [
            music_service_pb2.Playlist(id=p["id"], nome=p["nome"], usuario_id=p["usuario_id"], musicas=p["musicas"])
            for p in data["playlists"] if p["usuario_id"] == request.usuario_id
        ]
        return music_service_pb2.PlaylistsResponse(playlists=playlists)

    def GetMusicasPlaylist(self, request, context):
        playlist = next((p for p in data["playlists"] if p["id"] == request.playlist_id), None)
        if not playlist:
            return music_service_pb2.MusicasResponse(musicas=[])
        musicas = [
            music_service_pb2.Musica(id=m["id"], titulo=m["titulo"], artista=m["artista"])
            for m in data["musicas"] if m["id"] in playlist["musicas"]
        ]
        return music_service_pb2.MusicasResponse(musicas=musicas)

    def GetPlaylistsComMusica(self, request, context):
        playlists = [
            music_service_pb2.Playlist(id=p["id"], nome=p["nome"], usuario_id=p["usuario_id"], musicas=p["musicas"])
            for p in data["playlists"] if request.musica_id in p.get("musicas", [])
        ]
        return music_service_pb2.PlaylistsResponse(playlists=playlists)


def serve():
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    music_service_pb2_grpc.add_MusicServiceServicer_to_server(MusicServiceServicer(), server)

    # Habilita reflection para o Insomnia descobrir os métodos automaticamente
    SERVICE_NAMES = (
        music_service_pb2.DESCRIPTOR.services_by_name['MusicService'].full_name,
        reflection.SERVICE_NAME,
    )
    reflection.enable_server_reflection(SERVICE_NAMES, server)

    server.add_insecure_port('localhost:5003')
    server.start()
    print("Servidor gRPC rodando em localhost:5003 (reflection ativo)")
    server.wait_for_termination()

if __name__ == '__main__':
    serve()
