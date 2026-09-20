package magno.com.ve.facturacion.repository.impl;

import magno.com.ve.facturacion.domain.model.Usuario;
import magno.com.ve.facturacion.repository.UsuarioRepository;

import java.util.*;

public class UsuarioRepositoryMemoria implements UsuarioRepository {

    private final Map<Long, Usuario> almacen = new LinkedHashMap<>();
    private Long siguienteId = 1L;

    @Override
    public Usuario guardar(Usuario usuario) {
        if (usuario.getId() == null) {
            usuario.setId(siguienteId++);
        }
        almacen.put(usuario.getId(), usuario);
        return usuario;
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        return Optional.ofNullable(almacen.get(id));
    }

    @Override
    public List<Usuario> listarTodos() {
        return new ArrayList<>(almacen.values());
    }

    @Override
    public boolean eliminar(Long id) {
        return almacen.remove(id) != null;
    }

    @Override
    public long contar() {
        return almacen.size();
    }

    @Override
    public Optional<Usuario> buscarPorUsername(String username) {
        if (username == null) return Optional.empty();
        return almacen.values().stream()
            .filter(u -> username.equalsIgnoreCase(u.getUsername()))
            .findFirst();
    }

    @Override
    public boolean existeUsername(String username) {
        return buscarPorUsername(username).isPresent();
    }
}