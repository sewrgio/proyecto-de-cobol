package magno.com.ve.facturacion.repository.impl;

import magno.com.ve.facturacion.domain.model.Cliente;
import magno.com.ve.facturacion.repository.ClienteRepository;

import java.util.*;
import java.util.stream.Collectors;

public class ClienteRepositoryMemoria implements ClienteRepository {

    private final Map<Long, Cliente> almacen = new LinkedHashMap<>();
    private Long siguienteId = 1L;

    @Override
    public Cliente guardar(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente no puede ser nulo.");
        }

        if (cliente.getId() == null) {
            cliente.setId(siguienteId++);
        }

        // Verificar duplicado por cédula
        for (Cliente c : almacen.values()) {
            if (c.getCedula() != null
                && c.getCedula().equalsIgnoreCase(cliente.getCedula())
                && !c.getId().equals(cliente.getId())) {
                throw new IllegalStateException("Ya existe un cliente con la cédula: " + cliente.getCedula());
            }
        }

        almacen.put(cliente.getId(), cliente);
        return cliente;
    }

    @Override
    public Optional<Cliente> buscarPorId(Long id) {
        return Optional.ofNullable(almacen.get(id));
    }

    @Override
    public List<Cliente> listarTodos() {
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
    public Optional<Cliente> buscarPorCedula(String cedula) {
        if (cedula == null) return Optional.empty();
        String busq = cedula.trim().toUpperCase().replace("-", "").replace(" ", "");
        return almacen.values().stream()
            .filter(c -> c.getCedula() != null
                && c.getCedula().toUpperCase().replace("-", "").replace(" ", "").equals(busq))
            .findFirst();
    }

    @Override
    public boolean existeCedula(String cedula) {
        return buscarPorCedula(cedula).isPresent();
    }

    @Override
    public List<Cliente> buscarPorNombre(String texto) {
        if (texto == null || texto.trim().isEmpty()) return listarTodos();
        String busq = texto.trim().toLowerCase();
        return almacen.values().stream()
            .filter(c -> (c.getNombres() != null && c.getNombres().toLowerCase().contains(busq))
                      || (c.getApellidos() != null && c.getApellidos().toLowerCase().contains(busq)))
            .collect(Collectors.toList());
    }

    @Override
    public List<Cliente> listarActivos() {
        return almacen.values().stream()
            .filter(Cliente::isActivo)
            .collect(Collectors.toList());
    }
}