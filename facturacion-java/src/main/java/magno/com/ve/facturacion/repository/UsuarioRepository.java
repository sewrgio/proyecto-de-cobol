package magno.com.ve.facturacion.repository;

import magno.com.ve.facturacion.domain.model.Usuario;
import java.util.Optional;

public interface UsuarioRepository extends Repository<Usuario, Long> {

    Optional<Usuario> buscarPorUsername(String username);

    boolean existeUsername(String username);
}