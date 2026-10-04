package magno.com.ve.facturacion.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class TasaCambioService {

    private static final String URL_OFICIAL  = "https://ve.dolarapi.com/v1/dolares/oficial";
    private static final String URL_PARALELO = "https://ve.dolarapi.com/v1/dolares/paralelo";

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private final ObjectMapper mapper = new ObjectMapper();

    private double tasaOficial = 0.0;
    private double tasaParalelo = 0.0;

    public double obtenerTasaOficial() throws Exception {
        tasaOficial = consultar(URL_OFICIAL);
        return tasaOficial;
    }

    public double obtenerTasaParalelo() throws Exception {
        tasaParalelo = consultar(URL_PARALELO);
        return tasaParalelo;
    }

    private double consultar(String url) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(10))
                .header("Accept", "application/json")
                .GET().build();

        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() != 200) {
            throw new RuntimeException("HTTP " + res.statusCode() + " en " + url);
        }
        JsonNode root = mapper.readTree(res.body());
        // { "fuente": "oficial", "promedio": 36.50, ... }
        return root.path("promedio").asDouble(0.0);
    }

    public double getTasaOficial()  { return tasaOficial; }
    public double getTasaParalelo() { return tasaParalelo; }
    public double getTasa()         { return tasaOficial > 0 ? tasaOficial : tasaParalelo; }
}