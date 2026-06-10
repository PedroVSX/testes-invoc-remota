import time
import random

try:
    import grpc
except ImportError:
    grpc = None

from locust import HttpUser, User, task, between, events, TaskSet


# IDs exatos conforme sua especificação
USER_IDS = list(range(1, 51))
PLAYLIST_IDS = list(range(1, 51))
MUSIC_IDS = list(range(101, 151))


# ---------------------------------------------------------------------------
# Helpers para serialização manual de proto3 (sem .proto gerado no cliente)
# ---------------------------------------------------------------------------

def encode_varint(value: int) -> bytes:
    """Codifica um inteiro não negativo no formato varint do Protocol Buffers."""
    result = b""
    while True:
        bits = value & 0x7F
        value >>= 7
        if value:
            result += bytes([bits | 0x80])
        else:
            result += bytes([bits])
            break
    return result


def encode_int32_field(field_number: int, value: int) -> bytes:
    """Serializa um campo proto3 do tipo int32: tag (varint) + value (varint)."""
    tag = (field_number << 3) | 0  # wire type 0 = varint
    return encode_varint(tag) + encode_varint(value)


# ---------------------------------------------------------------------------
# TaskSet compartilhado por todos os protocolos
# ---------------------------------------------------------------------------

class UserBehavior(TaskSet):
    """
    Comportamento de carga definido:
      1. Listar todas as músicas        (peso 1)
      2. Listar todos os usuários       (peso 1)
      3. Listar playlists de um usuário (peso 5)
      4. Listar músicas de uma playlist (peso 3)
      5. Listar playlists com uma música(peso 1)
    """

    # ── REST / GraphQL / SOAP / gRPC dispatchers ──────────────────────────

    def call_list_musics(self):
        p = self.user.protocol
        if p == "rest":
            self.client.get("/musicas", name="REST_listAllMusics")
        elif p == "graphql":
            self.client.post("/graphql",
                             json={"query": "{ musicas { id titulo artista } }"},
                             name="GraphQL_listAllMusics")
        elif p == "soap":
            env = ('<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" '
                   'xmlns:soap="music.soap.service"><soapenv:Body>'
                   '<soap:get_musicas/></soapenv:Body></soapenv:Envelope>')
            self.client.post("/soap/service", data=env,
                             headers={"Content-Type": "text/xml"},
                             name="SOAP_listAllMusics")
        elif p == "grpc":
            self.grpc_call("/music.MusicService/GetMusicas", b"", "gRPC_listAllMusics")

    def call_list_users(self):
        p = self.user.protocol
        if p == "rest":
            self.client.get("/usuarios", name="REST_listAllUsers")
        elif p == "graphql":
            self.client.post("/graphql",
                             json={"query": "{ usuarios { id nome } }"},
                             name="GraphQL_listAllUsers")
        elif p == "soap":
            env = ('<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" '
                   'xmlns:soap="music.soap.service"><soapenv:Body>'
                   '<soap:get_usuarios/></soapenv:Body></soapenv:Envelope>')
            self.client.post("/soap/service", data=env,
                             headers={"Content-Type": "text/xml"},
                             name="SOAP_listAllUsers")
        elif p == "grpc":
            self.grpc_call("/music.MusicService/GetUsuarios", b"", "gRPC_listAllUsers")

    def call_list_playlists_by_user(self):
        p = self.user.protocol
        uid = random.choice(USER_IDS)
        if p == "rest":
            self.client.get(f"/usuarios/{uid}/playlists",
                            name="REST_listPlaylistsByUser")
        elif p == "graphql":
            q = f'{{ playlistsPorUsuario(usuario_id: {uid}) {{ id nome }} }}'
            self.client.post("/graphql", json={"query": q},
                             name="GraphQL_listPlaylistsByUser")
        elif p == "soap":
            env = (f'<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" '
                   f'xmlns:soap="music.soap.service"><soapenv:Body>'
                   f'<soap:get_playlists_usuario><uid>{uid}</uid>'
                   f'</soap:get_playlists_usuario></soapenv:Body></soapenv:Envelope>')
            self.client.post("/soap/service", data=env,
                             headers={"Content-Type": "text/xml"},
                             name="SOAP_listPlaylistsByUser")
        elif p == "grpc":
            self.grpc_call("/music.MusicService/GetPlaylistsUsuario",
                           encode_int32_field(1, uid),
                           "gRPC_listPlaylistsByUser")

    def call_list_musics_from_playlist(self):
        p = self.user.protocol
        pid = random.choice(PLAYLIST_IDS)
        if p == "rest":
            self.client.get(f"/playlists/{pid}/musicas",
                            name="REST_listMusicsFromPlaylist")
        elif p == "graphql":
            q = f'{{ musicasPorPlaylist(playlist_id: {pid}) {{ id titulo artista }} }}'
            self.client.post("/graphql", json={"query": q},
                             name="GraphQL_listMusicsFromPlaylist")
        elif p == "soap":
            env = (f'<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" '
                   f'xmlns:soap="music.soap.service"><soapenv:Body>'
                   f'<soap:get_musicas_playlist><pid>{pid}</pid>'
                   f'</soap:get_musicas_playlist></soapenv:Body></soapenv:Envelope>')
            self.client.post("/soap/service", data=env,
                             headers={"Content-Type": "text/xml"},
                             name="SOAP_listMusicsFromPlaylist")
        elif p == "grpc":
            self.grpc_call("/music.MusicService/GetMusicasPlaylist",
                           encode_int32_field(1, pid),
                           "gRPC_listMusicsFromPlaylist")

    def call_list_playlists_by_music(self):
        p = self.user.protocol
        mid = random.choice(MUSIC_IDS)
        if p == "rest":
            self.client.get(f"/musicas/{mid}/playlists",
                            name="REST_listPlaylistsByMusic")
        elif p == "graphql":
            q = f'{{ playlistsPorMusica(musica_id: {mid}) {{ id nome }} }}'
            self.client.post("/graphql", json={"query": q},
                             name="GraphQL_listPlaylistsByMusic")
        elif p == "soap":
            env = (f'<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" '
                   f'xmlns:soap="music.soap.service"><soapenv:Body>'
                   f'<soap:get_playlists_com_musica><mid>{mid}</mid>'
                   f'</soap:get_playlists_com_musica></soapenv:Body></soapenv:Envelope>')
            self.client.post("/soap/service", data=env,
                             headers={"Content-Type": "text/xml"},
                             name="SOAP_listPlaylistsByMusic")
        elif p == "grpc":
            self.grpc_call("/music.MusicService/GetPlaylistsComMusica",
                           encode_int32_field(1, mid),
                           "gRPC_listPlaylistsByMusic")

    # ── Tarefas com pesos ─────────────────────────────────────────────────

    @task(1)
    def task_list_musics(self):         self.call_list_musics()

    @task(1)
    def task_list_users(self):          self.call_list_users()

    @task(5)
    def task_playlists_by_user(self):   self.call_list_playlists_by_user()

    @task(3)
    def task_musics_from_playlist(self): self.call_list_musics_from_playlist()

    @task(1)
    def task_playlists_by_music(self):  self.call_list_playlists_by_music()

    # ── gRPC helper ───────────────────────────────────────────────────────

    def grpc_call(self, method: str, payload: bytes, name: str):
        if grpc is None:
            events.request.fire(
                request_type="gRPC", name=name,
                response_time=0, response_length=0,
                exception=Exception("Módulo 'grpc' não instalado. Execute: pip install grpcio"),
            )
            return

        start = time.perf_counter()
        try:
            stub = self.user.grpc_channel.unary_unary(
                method,
                request_serializer=lambda x: x,
                response_deserializer=lambda x: x,
            )
            stub(payload, timeout=5.0)
            elapsed = (time.perf_counter() - start) * 1000
            events.request.fire(
                request_type="gRPC", name=name,
                response_time=elapsed, response_length=0,
            )
        except Exception as exc:
            elapsed = (time.perf_counter() - start) * 1000
            events.request.fire(
                request_type="gRPC", name=name,
                response_time=elapsed, response_length=0,
                exception=exc,
            )


# ---------------------------------------------------------------------------
# Classes de Usuário Locust
# ---------------------------------------------------------------------------

class RestUser(HttpUser):
    protocol = "rest"
    wait_time = between(1, 2)
    tasks = [UserBehavior]


class GraphqlUser(HttpUser):
    protocol = "graphql"
    wait_time = between(1, 2)
    tasks = [UserBehavior]


class SoapUser(HttpUser):
    protocol = "soap"
    wait_time = between(1, 2)
    tasks = [UserBehavior]


class GrpcUser(User):
    """Usuário gRPC — herda de User (não HttpUser), abre canal uma vez por worker."""
    protocol = "grpc"
    wait_time = between(1, 2)
    tasks = [UserBehavior]
    # O --host passado ao Locust deve ser "host:porta" sem prefixo (ex: localhost:5007)

    grpc_channel = None

    def on_start(self):
        if grpc is not None:
            self.grpc_channel = grpc.insecure_channel(self.host)

    def on_stop(self):
        if self.grpc_channel is not None:
            self.grpc_channel.close()
            self.grpc_channel = None
