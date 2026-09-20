package magno.com.ve.facturacion.repository;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz genérica para todos los repositorios del sistema.
 */
public interface Repository<T, ID> {

    T guardar(T entidad);

    Optional<T> buscarPorId(ID id);

    List<T> listarTodos();

    boolean eliminar(ID id);

    long contar();
}