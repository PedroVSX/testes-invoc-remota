import json
import time
from locust import HttpUser, task, between
import grpc

class Q4MusicasPlaylistPerformanceUser(HttpUser):
    wait_time = between(0.5, 1.5)
    PLAYLIST_ID = "p1"
    
    @task
    def test_rest_q4(self):
        self.client.get(f"/api/playlists/{self.PLAYLIST_ID}/musics", name="1. REST - Q4: listMusicsFromPlaylist")

    @task
    def test_graphql_q4(self):
        headers = {"Content-Type": "application/json"}
        payload = {"query": f'{{ listMusicsFromPlaylist(playlistId: "{self.PLAYLIST_ID}") {{ id name artist }} }}'}
        self.client.post("/graphql", data=json.dumps(payload), headers=headers, name="2. GraphQL - Q4: listMusicsFromPlaylist")

    @task
    def test_soap_q4(self):
        headers = {"Content-Type": "text/xml;charset=UTF-8"}
        body = f"<soap:listMusicsFromPlaylistRequest><soap:playlistId>{self.PLAYLIST_ID}</soap:playlistId></soap:listMusicsFromPlaylistRequest>"
        envelope = f"""<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:soap="http://example.com/demo/soap">
            <soapenv:Header/>
            <soapenv:Body>{body}</soapenv:Body>
        </soapenv:Envelope>"""
        self.client.post("/ws/usuarios", data=envelope, headers=headers, name="3. SOAP - Q4: listMusicsFromPlaylist")

    @task
    def test_grpc_q4(self):
        start_time = time.perf_counter()
        channel = None
        try:
            channel = grpc.insecure_channel("localhost:9090")
            stub = channel.unary_unary(
                "/streaming.StreamingService/ListMusicsFromPlaylist",
                request_serializer=lambda x: x, response_deserializer=lambda x: x
            )
            encoded_playlist_id = b'\x0a' + bytes([len(self.PLAYLIST_ID)]) + self.PLAYLIST_ID.encode('utf-8')
            stub(encoded_playlist_id, timeout=3.0)
            total_time = (time.perf_counter() - start_time) * 1000
            self.environment.events.request.fire(
                request_type="gRPC", name="4. gRPC - Q4: listMusicsFromPlaylist", response_time=total_time, response_length=0, exception=None
            )
        except Exception as e:
            total_time = (time.perf_counter() - start_time) * 1000
            self.environment.events.request.fire(
                request_type="gRPC", name="4. gRPC - Q4: listMusicsFromPlaylist", response_time=total_time, response_length=0, exception=e
            )
        finally:
            if channel:
                channel.close()