package magno.com.ve.facturacion.integration.cobol;

import magno.com.ve.facturacion.integration.cobol.dto.FacturaRequestDTO;
import magno.com.ve.facturacion.integration.cobol.dto.FacturaResponseDTO;
import magno.com.ve.facturacion.integration.cobol.dto.ItemDTO;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Conector que ejecuta el COBOL localmente usando ProcessBuilder.
 * El flujo es:
 *   1. Java escribe "factura-input.json"
 *   2. Java ejecuta "./bin/proc-factura-json"
 *   3. COBOL lee el input y escribe "factura-output.json"
 *   4. Java lee el output y construye el FacturaResponseDTO
 */
public class CobolConnectorFile implements CobolConnector {

    // Ruta al ejecutable COBOL (relativa desde donde se ejecuta Java)
    private static final String COBOL_BIN = "bin/proc-factura-json";
    private static final String INPUT_FILE = "factura-input.json";
    private static final String OUTPUT_FILE = "factura-output.json";
    private static final int TIMEOUT_SEGUNDOS = 30;

    private final Path directorioCobol;

    public CobolConnectorFile() {
        // Por defecto, buscamos el directorio COBOL en ../facturacion-cobol
        this.directorioCobol = Paths.get("..", "facturacion-cobol").toAbsolutePath().normalize();
    }

    public CobolConnectorFile(String rutaCobol) {
        this.directorioCobol = Paths.get(rutaCobol).toAbsolutePath().normalize();
    }

    @Override
    public FacturaResponseDTO procesarFactura(FacturaRequestDTO request)
            throws IOException, CobolException {

        // Validar que exista el ejecutable COBOL
        Path binPath = directorioCobol.resolve(COBOL_BIN);
        if (!Files.exists(binPath)) {
            throw new IOException("No se encuentra el ejecutable COBOL: " + binPath);
        }

        // 1. Escribir el archivo de entrada
        Path inputPath = directorioCobol.resolve(INPUT_FILE);
        escribirInputJson(request, inputPath);

        // 2. Limpiar el archivo de salida anterior
        Path outputPath = directorioCobol.resolve(OUTPUT_FILE);
        Files.deleteIfExists(outputPath);

        // 3. Ejecutar el COBOL
        ejecutarCobol(binPath, directorioCobol);

        // 4. Leer el archivo de salida
        if (!Files.exists(outputPath)) {
            throw new IOException("COBOL no generó el archivo de salida: " + outputPath);
        }

        return leerOutputJson(outputPath);
    }

    /**
     * Genera el JSON de entrada con el formato que espera el COBOL.
     */
    private void escribirInputJson(FacturaRequestDTO req, Path path) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"nombreSucursal\": \"").append(safe(req.getNombreSucursal())).append("\",\n");
        sb.append("  \"rifSucursal\": \"").append(safe(req.getRifSucursal())).append("\",\n");
        sb.append("  \"direccionSucursal\": \"").append(safe(req.getDireccionSucursal())).append("\",\n");
        sb.append("  \"ciudadSucursal\": \"").append(safe(req.getCiudadSucursal())).append("\",\n");
        sb.append("  \"estadoSucursal\": \"").append(safe(req.getEstadoSucursal())).append("\",\n");
        sb.append("  \"zonaPostal\": \"").append(safe(req.getZonaPostal())).append("\",\n");
        sb.append("  \"cajeroCodigo\": \"").append(safe(req.getCajeroCodigo())).append("\",\n");
        sb.append("  \"cajeroNombre\": \"").append(safe(req.getCajeroNombre())).append("\",\n");
        sb.append("  \"cajaNumero\": \"").append(safe(req.getCajaNumero())).append("\",\n");
        sb.append("  \"rifCliente\": \"").append(safe(req.getRifCliente())).append("\",\n");
        sb.append("  \"razonSocial\": \"").append(safe(req.getRazonSocial())).append("\",\n");
        sb.append("  \"formaPago\": \"").append(safe(req.getFormaPago())).append("\",\n");
        sb.append("  \"fechaEmision\": \"").append(safe(req.getFechaEmision())).append("\",\n");
        sb.append("  \"horaEmision\": \"").append(safe(req.getHoraEmision())).append("\",\n");
        sb.append("  \"montoPagado\": \"").append(req.getMontoPagado()).append("\",\n");

        // Items
        if (req.getItems() != null) {
            for (int i = 0; i < req.getItems().size(); i++) {
                ItemDTO item = req.getItems().get(i);
                boolean esUltimo = (i == req.getItems().size() - 1);
                sb.append("  \"codigoItem\": \"").append(safe(item.getCodigo())).append("\",\n");
                sb.append("  \"nombreItem\": \"").append(safe(item.getNombre())).append("\",\n");
                sb.append("  \"cantidadItem\": \"").append(item.getCantidad()).append("\",\n");
                sb.append("  \"precioItem\": \"").append(item.getPrecio()).append("\",\n");
                sb.append("  \"alicuotaItem\": \"").append(safe(item.getAlicuota())).append("\"");
                if (!esUltimo) sb.append(",");
                sb.append("\n");
            }
        }

        sb.append("}\n");

        Files.writeString(path, sb.toString(), StandardCharsets.UTF_8);
    }

    /**
     * Ejecuta el binario COBOL.
     */
    private void ejecutarCobol(Path binPath, Path workingDir) throws IOException, CobolException {
        ProcessBuilder pb = new ProcessBuilder(binPath.toString());
        pb.directory(workingDir.toFile());
        pb.redirectErrorStream(true);

        Process proceso = pb.start();

        // Leer la salida (por si acaso)
        StringBuilder salida = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(proceso.getInputStream(), StandardCharsets.UTF_8))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                salida.append(linea).append("\n");
            }
        }

        try {
            boolean terminado = proceso.waitFor(TIMEOUT_SEGUNDOS, java.util.concurrent.TimeUnit.SECONDS);
            if (!terminado) {
                proceso.destroyForcibly();
                throw new CobolException("El COBOL tardó más de " + TIMEOUT_SEGUNDOS + " segundos", -1);
            }
            int exitCode = proceso.exitValue();
            if (exitCode != 0) {
                throw new CobolException(
                    "COBOL terminó con código " + exitCode + ":\n" + salida, -1);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Ejecución del COBOL interrumpida", e);
        }
    }

    /**
     * Lee el JSON de salida generado por COBOL.
     * Parser simple, específico para nuestro formato.
     */
    private FacturaResponseDTO leerOutputJson(Path path) throws IOException, CobolException {
        FacturaResponseDTO response = new FacturaResponseDTO();

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                linea = linea.trim();
                if (linea.contains("\"estado\"")) {
                    response.setEstado(extraerValor(linea));
                } else if (linea.contains("\"codigoError\"")) {
                    try { response.setCodigoError(Integer.parseInt(extraerValor(linea))); }
                    catch (NumberFormatException ignored) {}
                } else if (linea.contains("\"numeroControl\"")) {
                    response.setNumeroControl(extraerValor(linea));
                } else if (linea.contains("\"numeroFactura\"")) {
                    try { response.setNumeroFactura(Long.parseLong(extraerValor(linea))); }
                    catch (NumberFormatException ignored) {}
                } else if (linea.contains("\"baseImponible\"")) {
                    try { response.setBaseImponible(Double.parseDouble(extraerValor(linea))); }
                    catch (NumberFormatException ignored) {}
                } else if (linea.contains("\"baseExento\"")) {
                    try { response.setBaseExento(Double.parseDouble(extraerValor(linea))); }
                    catch (NumberFormatException ignored) {}
                } else if (linea.contains("\"iva\"")) {
                    try { response.setIva(Double.parseDouble(extraerValor(linea))); }
                    catch (NumberFormatException ignored) {}
                } else if (linea.contains("\"igtf\"")) {
                    try { response.setIgtf(Double.parseDouble(extraerValor(linea))); }
                    catch (NumberFormatException ignored) {}
                } else if (linea.contains("\"total\"")) {
                    try { response.setTotal(Double.parseDouble(extraerValor(linea))); }
                    catch (NumberFormatException ignored) {}
                }
            }
        }

        if (!response.esExitosa()) {
            throw new CobolException(
                "El COBOL devolvió error: " + response.getCodigoError(), response.getCodigoError());
        }

        return response;
    }

    /**
     * Extrae el valor de una línea JSON del tipo: "clave": valor
     */
    private String extraerValor(String linea) {
        // Buscar el ":" y extraer lo que está después
        int idx = linea.indexOf(':');
        if (idx < 0) return "";

        String valor = linea.substring(idx + 1).trim();
        // Quitar la coma final si la hay
        if (valor.endsWith(",")) {
            valor = valor.substring(0, valor.length() - 1).trim();
        }
        // Quitar comillas si las tiene
        if (valor.startsWith("\"") && valor.endsWith("\"")) {
            valor = valor.substring(1, valor.length() - 1);
        }
        return valor;
    }

    private String safe(String s) {
        return s != null ? s : "";
    }

    @Override
    public boolean estaDisponible() {
        Path binPath = directorioCobol.resolve(COBOL_BIN);
        return Files.exists(binPath);
    }
}