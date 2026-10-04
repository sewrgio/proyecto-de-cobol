package magno.com.ve.facturacion.integration.cobol;

import com.fasterxml.jackson.databind.ObjectMapper;
import magno.com.ve.facturacion.integration.cobol.dto.FacturaRequestDTO;
import magno.com.ve.facturacion.integration.cobol.dto.FacturaResponseDTO;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.Socket;

public class CobolConnectorSocket implements CobolConnector {

    private final String host;
    private final int puerto;
    private final ObjectMapper mapper = new ObjectMapper();

    public CobolConnectorSocket(String host, int puerto) {
        this.host = host;
        this.puerto = puerto;
    }

    @Override
    public FacturaResponseDTO procesarFactura(FacturaRequestDTO request)
            throws IOException, CobolException {
        try (Socket socket = new Socket(host, puerto);
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            
            // Enviar la petición JSON por TCP
            out.println(mapper.writeValueAsString(request));
            
            // Leer la respuesta
            String jsonResponse = in.readLine();
            if (jsonResponse == null) {
                throw new IOException("El servidor cerró la conexión sin responder");
            }
            
            return mapper.readValue(jsonResponse, FacturaResponseDTO.class);
        }
    }

    @Override
    public boolean estaDisponible() {
        try (Socket socket = new Socket(host, puerto)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
