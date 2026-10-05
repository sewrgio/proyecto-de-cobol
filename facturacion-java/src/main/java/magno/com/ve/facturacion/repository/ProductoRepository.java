package magno.com.ve.facturacion.repository;

import magno.com.ve.facturacion.domain.model.Producto;
import java.util.List;
import java.util.Optional;

public interface ProductoRepository {
    Optional<Producto> buscarPorId(String id);
    List<Producto> buscarPorNombre(String nombre);
    List<Producto> listarTodos();
    void guardar(Producto producto);
}