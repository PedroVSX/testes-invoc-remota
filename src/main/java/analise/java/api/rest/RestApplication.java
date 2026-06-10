package analise.java.api.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "analise.java")
public class RestApplication {

    public static void main(String[] args) {
        // Porta padrão 8081, pode ser alterada via argumento --server.port=XXXX
        SpringApplication app = new SpringApplication(RestApplication.class);

        // Define porta padrão se não for especificada
        if (args.length == 0 || !containsPortArg(args)) {
            app.setDefaultProperties(java.util.Map.of("server.port", "5000"));
        }

        app.run(args);
    }

    private static boolean containsPortArg(String[] args) {
        for (String arg : args) {
            if (arg.startsWith("--server.port=")) {
                return true;
            }
        }
        return false;
    }
}