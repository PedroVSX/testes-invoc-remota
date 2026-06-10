from spyne import Application, rpc, ServiceBase, Integer, Unicode, Array
from spyne.model import ComplexModel
from spyne.protocol.soap import Soap11
from spyne.server.wsgi import WsgiApplication
from wsgiref.simple_server import make_server
import json


# ── Modelos XML ──────────────────────────────────────────────────────────────

class Usuario(ComplexModel):
    class Attributes(ComplexModel.Attributes):
        pass
    id   = Integer
    nome = Unicode


class Musica(ComplexModel):
    class Attributes(ComplexModel.Attributes):
        pass
    id      = Integer
    titulo  = Unicode
    artista = Unicode


class Playlist(ComplexModel):
    class Attributes(ComplexModel.Attributes):
        pass
    id         = Integer
    usuario_id = Integer
    nome       = Unicode
    musicas    = Array(Integer)


# ── Dados ────────────────────────────────────────────────────────────────────

def carregar_dados():
    with open('t_6/dataset.json', 'r', encoding='utf-8') as f:
        return json.load(f)

data = carregar_dados()


# ── Serviço ──────────────────────────────────────────────────────────────────

class MusicService(ServiceBase):

    @rpc(_returns=Array(Usuario))
    def get_usuarios(self):
        return [Usuario(id=u['id'], nome=u['nome']) for u in data['usuarios']]

    @rpc(_returns=Array(Musica))
    def get_musicas(self):
        return [Musica(id=m['id'], titulo=m['titulo'], artista=m['artista'])
                for m in data['musicas']]

    @rpc(Integer, _returns=Array(Playlist))
    def get_playlists_usuario(self, usuario_id):
        playlists = [p for p in data['playlists'] if p['usuario_id'] == usuario_id]
        return [Playlist(id=p['id'], usuario_id=p['usuario_id'],
                         nome=p['nome'], musicas=p['musicas'])
                for p in playlists]

    @rpc(Integer, _returns=Array(Musica))
    def get_musicas_playlist(self, playlist_id):
        playlist = next((p for p in data['playlists'] if p['id'] == playlist_id), None)
        if not playlist:
            return []
        ids = set(playlist['musicas'])
        return [Musica(id=m['id'], titulo=m['titulo'], artista=m['artista'])
                for m in data['musicas'] if m['id'] in ids]

    @rpc(Integer, _returns=Array(Playlist))
    def get_playlists_com_musica(self, musica_id):
        playlists = [p for p in data['playlists'] if musica_id in p['musicas']]
        return [Playlist(id=p['id'], usuario_id=p['usuario_id'],
                         nome=p['nome'], musicas=p['musicas'])
                for p in playlists]


# ── Configuração ─────────────────────────────────────────────────────────────

application = Application(
    [MusicService],
    'music.soap.service',
    in_protocol=Soap11(validator='soft'),
    out_protocol=Soap11()
)

if __name__ == '__main__':
    wsgi_app = WsgiApplication(application)
    server = make_server('localhost', 5002, wsgi_app)
    print("Servidor SOAP rodando em http://localhost:5002/?wsdl")
    server.serve_forever()