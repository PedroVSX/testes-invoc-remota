import json
import time
from locust import HttpUser, task, between
import grpc

class Q5PlaylistsMusicaPerformanceUser(HttpUser):
    wait_time = between(0.5, 1.5)
    MUSIC_ID = "m1"
    
    @task
    def test_rest_q5(self):
        self.client.get(f"/api/musics/{self.MUSIC_ID}/playlists", name="1. REST - Q5: listPlaylistsByMusic")

    @task
    def test_graphql_q5(self):
        headers = {"Content-Type": "application/json"}
        payload = {"query": f'{{ listPlaylistsByMusic(musicId: "{self.MUSIC_ID}") {{ id name }} }}'}
        self.client.post("/graphql", data=json.dumps(payload), headers=headers, name="2. GraphQL - Q5: listPlaylistsByMusic")

    @task
    def test_soap_q5(self):
        headers = {"Content-Type": "text/xml;charset=UTF-8"}
        body = f"<soap:listPlaylistsByMusicRequest><soap:musicId>{self.MUSIC_ID}</soap:musicId></soap:listPlaylistsByMusicRequest>"
        envelope = f"""<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:soap="http://example.com/demo/soap">
            <soapenv:Header/>
            <soapenv:Body>{body}</soapenv:Body>
        </soapenv:Envelope>"""
        self.client.post("/ws/usuarios", data=envelope, headers=headers, name="3. SOAP - Q5: listPlaylistsByMusic")

    @task
    def test_grpc_q5(self):
        start_time = time.perf_counter()
        channel = None
        try:
            channel = grpc.insecure_channel("localhost:9090")
            stub = channel.unary_unary(
                "/streaming.StreamingService/ListPlaylistsByMusic",
                request_serializer=lambda x: x, response_deserializer=lambda x: x
            )
            encoded_music_id = b'\x0a' + bytes([len(self.MUSIC_ID)]) + self.MUSIC_ID.encode('utf-8')
            stub(encoded_music_id, timeout=3.0)
            total_time = (time.perf_counter() - start_time) * 1000
            self.environment.events.request.fire(
                request_type="gRPC", name="4. gRPC - Q5: listPlaylistsByMusic", response_time=total_time, response_length=0, exception=None
            )
        except Exception as e:
            total_time = (time.perf_counter() - start_time) * 1000
            self.environment.events.request.fire(
                request_type="gRPC", name="4. gRPC - Q5: listPlaylistsByMusic", response_time=total_time, response_length=0, exception=e
            )
        finally:
            if channel:
                channel.close()