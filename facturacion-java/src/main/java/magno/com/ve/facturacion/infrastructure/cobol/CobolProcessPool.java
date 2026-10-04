package magno.com.ve.facturacion.infrastructure.cobol;

import com.fasterxml.jackson.databind.ObjectMapper;
import magno.com.ve.facturacion.integration.cobol.dto.FacturaRequestDTO;
import magno.com.ve.facturacion.integration.cobol.dto.FacturaResponseDTO;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class CobolProcessPool {
    private final BlockingQueue<CobolProcess> pool;
    private final int poolSize;
    private final ObjectMapper mapper;

    public CobolProcessPool(int poolSize) {
        this.poolSize = poolSize;
        this.pool = new ArrayBlockingQueue<>(poolSize);
        this.mapper = new ObjectMapper();
        initializePool();
    }

    private void initializePool() {
        for (int i = 0; i < poolSize; i++) {
            try {
                // Inicia el proceso COBOL en background esperando JSON por stdin
                ProcessBuilder pb = new ProcessBuilder("./PROC-FACTURA-JSON");
                pb.directory(new java.io.File("../facturacion-cobol/bin")); 
                Process p = pb.start();
                pool.offer(new CobolProcess(p));
            } catch (Exception e) {
                System.err.println("Error iniciando proceso COBOL: " + e.getMessage());
            }
        }
    }

    public FacturaResponseDTO procesarFactura(FacturaRequestDTO request) throws Exception {
        CobolProcess cp = pool.poll(5, TimeUnit.SECONDS);
        if (cp == null) {
            throw new RuntimeException("Timeout esperando proceso COBOL disponible");
        }

        try {
            String jsonRequest = mapper.writeValueAsString(request);
            cp.writer.println(jsonRequest);
            cp.writer.flush();

            String jsonResponse = cp.reader.readLine();
            return mapper.readValue(jsonResponse, FacturaResponseDTO.class);
        } finally {
            // Devolver proceso al pool
            pool.offer(cp);
        }
    }

    public void shutdown() {
        for (CobolProcess cp : pool) {
            cp.process.destroy();
        }
    }

    private static class CobolProcess {
        final Process process;
        final PrintWriter writer;
        final BufferedReader reader;

        CobolProcess(Process process) {
            this.process = process;
            this.writer = new PrintWriter(new OutputStreamWriter(process.getOutputStream()));
            this.reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        }
    }
}
