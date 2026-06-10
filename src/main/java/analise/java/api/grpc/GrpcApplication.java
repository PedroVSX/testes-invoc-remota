package analise.java.api.grpc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "analise.java")
public class GrpcApplication {

    public static void main(String[] args) {
        // Porta padrão 5007 para gRPC, pode ser alterada via argumento --grpc.server.port=XXXX
        SpringApplication app = new SpringApplication(GrpcApplication.class);

        // Define porta padrão se não for especificada
        if (args.length == 0 || !containsPortArg(args)) {
            app.setDefaultProperties(java.util.Map.of("grpc.server.port", "5000"));
        }

        app.run(args);
    }

    private static boolean containsPortArg(String[] args) {
        for (String arg : args) {
            if (arg.startsWith("--grpc.server.port=")) {
                return true;
            }
        }
        return false;
    }
}
