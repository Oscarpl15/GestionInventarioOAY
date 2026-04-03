package com.oay.gestioninventariooay.service;

import com.oay.gestioninventariooay.dao.InventarioDAO;
import com.oay.gestioninventariooay.model.Aceite;
import com.oay.gestioninventariooay.model.Herramienta;
import com.oay.gestioninventariooay.model.Material;

import java.util.List;

public class InventarioService {
    private final InventarioDAO dao = new InventarioDAO();

    // Herramientas
    public void guardarHerramienta(Herramienta h) throws Exception { dao.guardarOActualizar(h); }
    public List<Herramienta> obtenerHerramientas() { return dao.obtenerTodos(Herramienta.class); }

    // --- MATERIALES ---
    public void guardarMaterial(Material m) throws Exception { dao.guardarOActualizar(m); }
    public List<Material> obtenerMateriales() { return dao.obtenerTodos(Material.class); }
    public void eliminarMaterial(Material m) throws Exception { dao.eliminar(Material.class, m.getId()); }

    // --- ACEITES ---
    public void guardarAceite(Aceite a) throws Exception { dao.guardarOActualizar(a); }
    public List<Aceite> obtenerAceites() { return dao.obtenerTodos(Aceite.class); }
    public void eliminarAceite(Aceite a) throws Exception { dao.eliminar(Aceite.class, a.getId()); }

    public void sumarStockHerramienta(Herramienta h) throws Exception {
        h.setCantidad(h.getCantidad() + 1);
        dao.guardarOActualizar(h);
    }
    public void restarStockHerramienta(Herramienta h) throws Exception {
        if(h.getCantidad() > 0) {
            h.setCantidad(h.getCantidad() - 1);
            dao.guardarOActualizar(h);
        }
    }

    public Material obtenerMaterialPorId(Integer id) {
        try (var em = com.oay.gestioninventariooay.util.GestorBBDD.getEntityManagerFactory().createEntityManager()) {
            return em.find(Material.class, id);
        }
    }

    public Herramienta obtenerHerramientaPorId(Integer id) {
        try (var em = com.oay.gestioninventariooay.util.GestorBBDD.getEntityManagerFactory().createEntityManager()) {
            return em.find(Herramienta.class, id);
        }
    }

    public Aceite obtenerAceitePorId(Integer id) {
        try (var em = com.oay.gestioninventariooay.util.GestorBBDD.getEntityManagerFactory().createEntityManager()) {
            return em.find(Aceite.class, id);
        }
    }
}
