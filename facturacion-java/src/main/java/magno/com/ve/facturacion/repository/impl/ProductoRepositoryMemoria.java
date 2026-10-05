package magno.com.ve.facturacion.repository.impl;

import magno.com.ve.facturacion.domain.model.Producto;
import magno.com.ve.facturacion.repository.ProductoRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ProductoRepositoryMemoria implements ProductoRepository {

    private final Map<String, Producto> datos = new ConcurrentHashMap<>();

    @Override
    public Optional<Producto> buscarPorId(String id) {
        return Optional.ofNullable(datos.get(id));
    }

    @Override
    public List<Producto> buscarPorNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) return listarTodos();
        String q = nombre.toLowerCase();
        return datos.values().stream()
            .filter(p -> p.getNombre().toLowerCase().contains(q)
                      || p.getId().toLowerCase().contains(q))
            .collect(Collectors.toList());
    }

    @Override
    public List<Producto> listarTodos() {
        return new ArrayList<>(datos.values());
    }

    @Override
    public void guardar(Producto producto) {
        datos.put(producto.getId(), producto);
    }
}
