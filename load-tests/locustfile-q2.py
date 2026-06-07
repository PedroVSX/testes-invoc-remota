import json
import time
from locust import HttpUser, task, between
import grpc

class Q2MusicasPerformanceUser(HttpUser):
    wait_time = between(0.5, 1.5)
    
    @task
    def test_rest_q2(self):
        self.client.get("/api/musics", name="1. REST - Q2: listAllMusics")

    @task
    def test_graphql_q2(self):
        headers = {"Content-Type": "application/json"}
        payload = {"query": "{ listAllMusics { id name artist } }"}
        self.client.post("/graphql", data=json.dumps(payload), headers=headers, name="2. GraphQL - Q2: listAllMusics")

    @task
    def test_soap_q2(self):
        headers = {"Content-Type": "text/xml;charset=UTF-8"}
        envelope = """<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:soap="http://example.com/demo/soap">
            <soapenv:Header/>
            <soapenv:Body><soap:listAllMusicsRequest/></soapenv:Body>
        </soapenv:Envelope>"""
        self.client.post("/ws/usuarios", data=envelope, headers=headers, name="3. SOAP - Q2: listAllMusics")

    @task
    def test_grpc_q2(self):
        start_time = time.perf_counter()
        channel = None
        try:
            channel = grpc.insecure_channel("localhost:9090")
            stub = channel.unary_unary(
                "/streaming.StreamingService/ListAllMusics",
                request_serializer=lambda x: x, response_deserializer=lambda x: x
            )
            stub(b"", timeout=3.0)
            total_time = (time.perf_counter() - start_time) * 1000
            self.environment.events.request.fire(
                request_type="gRPC", name="4. gRPC - Q2: listAllMusics", response_time=total_time, response_length=0, exception=None
            )
        except Exception as e:
            total_time = (time.perf_counter() - start_time) * 1000
            self.environment.events.request.fire(
                request_type="gRPC", name="4. gRPC - Q2: listAllMusics", response_time=total_time, response_length=0, exception=e
            )
        finally:
            if channel:
                channel.close()