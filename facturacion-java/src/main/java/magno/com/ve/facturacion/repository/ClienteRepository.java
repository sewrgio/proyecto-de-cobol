package magno.com.ve.facturacion.repository;

import magno.com.ve.facturacion.domain.model.Cliente;
import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends Repository<Cliente, Long> {

    Optional<Cliente> buscarPorCedula(String cedula);

    boolean existeCedula(String cedula);

    List<Cliente> buscarPorNombre(String texto);

    List<Cliente> listarActivos();
}