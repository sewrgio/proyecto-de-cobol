package magno.com.ve.facturacion.service;

import magno.com.ve.facturacion.domain.model.ProductoInventario;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio que lee el inventario.dat (generado por COBOL) y permite búsquedas rápidas.
 */
public class InventarioService {

    // Ruta al archivo generado por COBOL
    private static final String INVENTARIO_FILE = "../facturacion-cobol/datos/inventario.dat";

    private final List<ProductoInventario> productos = new ArrayList<>();
    private boolean cargado = false;

    public void cargar() throws IOException {
        if (cargado) return;

        Path path = Paths.get(INVENTARIO_FILE);
        if (!Files.exists(path)) {
            // Intentar ruta alternativa
            path = Paths.get("facturacion-cobol/datos/inventario.dat");
        }
        if (!Files.exists(path)) {
            throw new IOException("No se encuentra el archivo de inventario: " + path.toAbsolutePath());
        }

        List<String> lineas = Files.readAllLines(path, StandardCharsets.UTF_8);

        for (String linea : lineas) {
            if (linea.length() < 70) continue;

            try {
                String codigo = linea.substring(0, 12).trim();
                String nombre = linea.substring(12, 52).trim();
                long precioCentavos = Long.parseLong(linea.substring(52, 62).trim());
                int stock = Integer.parseInt(linea.substring(62, 68).trim());
                String alicuota = linea.substring(68, 69).trim();
                String categoria = linea.substring(69, Math.min(84, linea.length())).trim();

                double precio = precioCentavos / 100.0;
                productos.add(new ProductoInventario(codigo, nombre, precio, stock, alicuota, categoria));
            } catch (Exception e) {
                System.err.println("Error parseando línea: [" + linea + "] → " + e.getMessage());
            }
        }

        cargado = true;
        System.out.println("✅ Inventario cargado: " + productos.size() + " productos");
    }

    /**
     * Busca productos por código, nombre o categoría (parcial, case-insensitive).
     */
    public List<ProductoInventario> buscar(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return new ArrayList<>(productos);
        }
        String query = texto.trim().toLowerCase();
        return productos.stream()
            .filter(p -> p.getCodigo().toLowerCase().contains(query)
                      || p.getNombre().toLowerCase().contains(query)
                      || p.getCategoria().toLowerCase().contains(query))
            .limit(20)
            .collect(Collectors.toList());
    }

    /**
     * Busca por código exacto.
     */
    public ProductoInventario buscarPorCodigo(String codigo) {
        if (codigo == null) return null;
        return productos.stream()
            .filter(p -> p.getCodigo().equals(codigo))
            .findFirst()
            .orElse(null);
    }

    public List<ProductoInventario> listarTodos() {
        return new ArrayList<>(productos);
    }

    public int getTotal() {
        return productos.size();
    }
}
