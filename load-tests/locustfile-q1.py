import json
import time
from locust import HttpUser, task, between
import grpc

class Q1UsuariosPerformanceUser(HttpUser):
    wait_time = between(0.5, 1.5)
    
    # REST
    @task
    def test_rest_q1(self):
        self.client.get("/api/users", name="1. REST - Q1: listAllUsers")

    # GraphQL
    @task
    def test_graphql_q1(self):
        headers = {"Content-Type": "application/json"}
        payload = {"query": "{ listAllUsers { id name } }"}
        self.client.post("/graphql", data=json.dumps(payload), headers=headers, name="2. GraphQL - Q1: listAllUsers")

    # SOAP
    @task
    def test_soap_q1(self):
        headers = {"Content-Type": "text/xml;charset=UTF-8"}
        envelope = """<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:soap="http://example.com/demo/soap">
            <soapenv:Header/>
            <soapenv:Body><soap:listAllUsersRequest/></soapenv:Body>
        </soapenv:Envelope>"""
        self.client.post("/ws/usuarios", data=envelope, headers=headers, name="3. SOAP - Q1: listAllUsers")

    # gRPC
    @task
    def test_grpc_q1(self):
        start_time = time.perf_counter()
        channel = None
        try:
            channel = grpc.insecure_channel("localhost:9090")
            stub = channel.unary_unary(
                "/streaming.StreamingService/ListAllUsers", # Mantenha a inicial minúscula que deu certo!
                request_serializer=lambda x: x,
                response_deserializer=lambda x: x
            )
            stub(b"", timeout=3.0)
            total_time = (time.perf_counter() - start_time) * 1000
            self.environment.events.request.fire(
                request_type="gRPC", name="4. gRPC - Q1: listAllUsers", response_time=total_time, response_length=0, exception=None
            )
        except Exception as e:
            total_time = (time.perf_counter() - start_time) * 1000
            self.environment.events.request.fire(
                request_type="gRPC", name="4. gRPC - Q1: listAllUsers", response_time=total_time, response_length=0, exception=e
            )
        finally:
            if channel:
                channel.close()