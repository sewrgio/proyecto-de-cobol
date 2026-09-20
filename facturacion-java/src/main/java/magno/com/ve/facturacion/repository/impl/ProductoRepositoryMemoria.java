package magno.com.ve.facturacion.repository.impl;

import magno.com.ve.facturacion.domain.model.Producto;
import magno.com.ve.facturacion.repository.ProductoRepository;

import java.util.*;
import java.util.stream.Collectors;

public class ProductoRepositoryMemoria implements ProductoRepository {

    private final Map<Long, Producto> almacen = new LinkedHashMap<>();
    private Long siguienteId = 1L;

    @Override
    public Producto guardar(Producto producto) {
        if (producto == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo.");
        }

        if (producto.getId() == null) {
            producto.setId(siguienteId++);
        }

        // Verificar duplicado por código
        for (Producto p : almacen.values()) {
            if (p.getCodigo() != null
                && p.getCodigo().equals(producto.getCodigo())
                && !p.getId().equals(producto.getId())) {
                throw new IllegalStateException("Ya existe un producto con el código: " + producto.getCodigo());
            }
        }

        almacen.put(producto.getId(), producto);
        return producto;
    }

    @Override
    public Optional<Producto> buscarPorId(Long id) {
        return Optional.ofNullable(almacen.get(id));
    }

    @Override
    public List<Producto> listarTodos() {
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
    public Optional<Producto> buscarPorCodigo(String codigo) {
        if (codigo == null) return Optional.empty();
        return almacen.values().stream()
            .filter(p -> codigo.equalsIgnoreCase(p.getCodigo()))
            .findFirst();
    }

    @Override
    public boolean existeCodigo(String codigo) {
        return buscarPorCodigo(codigo).isPresent();
    }

    @Override
    public List<Producto> buscarPorNombre(String texto) {
        if (texto == null || texto.trim().isEmpty()) return listarTodos();
        String busq = texto.trim().toLowerCase();
        return almacen.values().stream()
            .filter(p -> p.getNombre() != null && p.getNombre().toLowerCase().contains(busq))
            .collect(Collectors.toList());
    }

    @Override
    public List<Producto> buscarPorPaisOrigen(String pais) {
        if (pais == null || pais.trim().isEmpty()) return listarTodos();
        return almacen.values().stream()
            .filter(p -> pais.equalsIgnoreCase(p.getPaisOrigen()))
            .collect(Collectors.toList());
    }

    @Override
    public List<Producto> buscarPorCompania(String compania) {
        if (compania == null || compania.trim().isEmpty()) return listarTodos();
        String busq = compania.trim().toLowerCase();
        return almacen.values().stream()
            .filter(p -> p.getCompaniaFabricacion() != null
                      && p.getCompaniaFabricacion().toLowerCase().contains(busq))
            .collect(Collectors.toList());
    }
}