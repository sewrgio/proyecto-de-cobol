package magno.com.ve.facturacion.integration.cobol;

import com.fasterxml.jackson.databind.ObjectMapper;
import magno.com.ve.facturacion.integration.cobol.dto.FacturaRequestDTO;
import magno.com.ve.facturacion.integration.cobol.dto.FacturaResponseDTO;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Implementación real del CobolConnector.
 * Se conecta por socket TCP al servidor COBOL (o al bridge C que llama a COBOL).
 *
 * NOTA: Esta clase se activará cuando el servidor COBOL esté listo.
 * Por ahora queda como esqueleto para que el equipo sepa cómo funcionará.
 */
public class CobolConnectorSocket implements CobolConnector {

    private final String host;
    private final int puerto;
    private final int timeoutMs;
    private final ObjectMapper mapper = new ObjectMapper();

    public CobolConnectorSocket(String host, int puerto) {
        this(host, puerto, 5000);
    }

    public CobolConnectorSocket(String host, int puerto, int timeoutMs) {
        this.host = host;
        this.puerto = puerto;
        this.timeoutMs = timeoutMs;
    }

    @Override
    public FacturaResponseDTO procesarFactura(FacturaRequestDTO request)
            throws IOException, CobolException {

        try (Socket socket = new Socket(host, puerto)) {
            socket.setSoTimeout(timeoutMs);

            // 1. Serializar el request a JSON
            String jsonRequest = mapper.writeValueAsString(request);

            // 2. Enviar JSON (una línea terminada en \n)
            try (BufferedWriter out = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8))) {
                out.write(jsonRequest);
                out.newLine();
                out.flush();
            }

            // 3. Leer la respuesta (una línea de JSON)
            String jsonResponse;
            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {
                jsonResponse = in.readLine();
            }

            if (jsonResponse == null || jsonResponse.isEmpty()) {
                throw new IOException("El servidor COBOL no respondió");
            }

            // 4. Deserializar respuesta
            FacturaResponseDTO response = mapper.readValue(jsonResponse, FacturaResponseDTO.class);

            // 5. Verificar si el COBOL reportó error de negocio
            if (!response.esExitosa()) {
                throw new CobolException(response.getMensaje(), response.getCodigoError());
            }

            return response;
        }
    }

    @Override
    public boolean estaDisponible() {
        try (Socket socket = new Socket(host, puerto)) {
            return socket.isConnected();
        } catch (IOException e) {
            return false;
        }
    }
}