package magno.com.ve.facturacion.service;

import magno.com.ve.facturacion.domain.enums.Rol;
import magno.com.ve.facturacion.domain.model.Usuario;
import magno.com.ve.facturacion.exception.ValidacionException;
import magno.com.ve.facturacion.repository.UsuarioRepository;
import magno.com.ve.facturacion.repository.impl.UsuarioRepositoryMemoria;

import java.security.MessageDigest;
import java.util.Optional;

public class UsuarioService {

    private final UsuarioRepository repository;

    public UsuarioService() {
        this.repository = new UsuarioRepositoryMemoria();
        inicializarUsuariosPorDefecto();
    }

    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }

    /**
     * Autentica un usuario. Devuelve el usuario si las credenciales son correctas.
     */
    public Usuario autenticar(String username, String password) throws ValidacionException {
        if (username == null || username.trim().isEmpty()) {
            throw new ValidacionException("El usuario es obligatorio.");
        }
        if (password == null || password.isEmpty()) {
            throw new ValidacionException("La contraseña es obligatoria.");
        }

        Optional<Usuario> opt = repository.buscarPorUsername(username.trim());
        if (opt.isEmpty()) {
            throw new ValidacionException("Usuario o contraseña incorrectos.");
        }

        Usuario usuario = opt.get();
        if (!usuario.isActivo()) {
            throw new ValidacionException("El usuario está inactivo. Contacte al administrador.");
        }

        String hash = hashPassword(password);
        if (!hash.equals(usuario.getPasswordHash())) {
            throw new ValidacionException("Usuario o contraseña incorrectos.");
        }

        return usuario;
    }

    public Usuario registrar(String username, String password, String nombreCompleto, Rol rol)
            throws ValidacionException {
        if (repository.existeUsername(username)) {
            throw new ValidacionException("Ya existe un usuario con ese nombre.");
        }
        if (password == null || password.length() < 4) {
            throw new ValidacionException("La contraseña debe tener al menos 4 caracteres.");
        }
        Usuario usuario = new Usuario(username, hashPassword(password), nombreCompleto, rol);
        return repository.guardar(usuario);
    }

    /**
     * Hash de contraseña con SHA-256.
     * Nota: en producción usar BCrypt o Argon2.
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error al hashear contraseña", e);
        }
    }

    /**
     * Crea usuarios por defecto para que puedas entrar al sistema.
     */
    private void inicializarUsuariosPorDefecto() {
        try {
            registrar("admin", "admin", "Administrador del Sistema", Rol.ADMINISTRADOR);
            registrar("cajero", "cajero", "Cajero de Turno", Rol.CAJERO);
            registrar("almacen", "almacen", "Encargado del Almacén", Rol.ALMACENISTA);
        } catch (ValidacionException e) {
            // Ya existen
        }
    }
}