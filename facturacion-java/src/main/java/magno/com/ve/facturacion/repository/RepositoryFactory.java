package magno.com.ve.facturacion.repository;

import magno.com.ve.facturacion.repository.impl.ClienteRepositoryMemoria;
import magno.com.ve.facturacion.repository.impl.ProductoRepositoryMemoria;

/**
 * Fábrica de repositorios (Singleton).
 * Comparte las mismas instancias entre todos los paneles.
 */
public class RepositoryFactory {

    private static final ClienteRepository clienteRepo = new ClienteRepositoryMemoria();
    private static final ProductoRepository productoRepo = new ProductoRepositoryMemoria();

    public static ClienteRepository getClienteRepository() {
        return clienteRepo;
    }

    public static ProductoRepository getProductoRepository() {
        return productoRepo;
    }

    private RepositoryFactory() {}
}
