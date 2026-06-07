import json
import time
from locust import HttpUser, task, between
import grpc

class Q3PlaylistsUsuarioPerformanceUser(HttpUser):
    wait_time = between(0.5, 1.5)
    USER_ID = "u1"
    
    @task
    def test_rest_q3(self):
        self.client.get(f"/api/users/{self.USER_ID}/playlists", name="1. REST - Q3: listPlaylistsByUser")

    @task
    def test_graphql_q3(self):
        headers = {"Content-Type": "application/json"}
        payload = {"query": f'{{ listPlaylistsByUser(userId: "{self.USER_ID}") {{ id name }} }}'}
        self.client.post("/graphql", data=json.dumps(payload), headers=headers, name="2. GraphQL - Q3: listPlaylistsByUser")

    @task
    def test_soap_q3(self):
        headers = {"Content-Type": "text/xml;charset=UTF-8"}
        body = f"<soap:listPlaylistsByUserRequest><soap:userId>{self.USER_ID}</soap:userId></soap:listPlaylistsByUserRequest>"
        envelope = f"""<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:soap="http://example.com/demo/soap">
            <soapenv:Header/>
            <soapenv:Body>{body}</soapenv:Body>
        </soapenv:Envelope>"""
        self.client.post("/ws/usuarios", data=envelope, headers=headers, name="3. SOAP - Q3: listPlaylistsByUser")

    @task
    def test_grpc_q3(self):
        start_time = time.perf_counter()
        channel = None
        try:
            channel = grpc.insecure_channel("localhost:9090")
            stub = channel.unary_unary(
                "/streaming.StreamingService/ListPlaylistsByUser",
                request_serializer=lambda x: x, response_deserializer=lambda x: x
            )
            # Tag binária Protobuf para String do campo userId
            encoded_user_id = b'\x0a' + bytes([len(self.USER_ID)]) + self.USER_ID.encode('utf-8')
            stub(encoded_user_id, timeout=3.0)
            total_time = (time.perf_counter() - start_time) * 1000
            self.environment.events.request.fire(
                request_type="gRPC", name="4. gRPC - Q3: listPlaylistsByUser", response_time=total_time, response_length=0, exception=None
            )
        except Exception as e:
            total_time = (time.perf_counter() - start_time) * 1000
            self.environment.events.request.fire(
                request_type="gRPC", name="4. gRPC - Q3: listPlaylistsByUser", response_time=total_time, response_length=0, exception=e
            )
        finally:
            if channel:
                channel.close()