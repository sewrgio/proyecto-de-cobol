package magno.com.ve.facturacion.repository;

import magno.com.ve.facturacion.domain.model.Producto;
import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends Repository<Producto, Long> {

    Optional<Producto> buscarPorCodigo(String codigo);

    boolean existeCodigo(String codigo);

    List<Producto> buscarPorNombre(String texto);

    List<Producto> buscarPorPaisOrigen(String pais);

    List<Producto> buscarPorCompania(String compania);
}