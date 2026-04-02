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

    // --- ACEITES ---
    public void guardarAceite(Aceite a) throws Exception { dao.guardarOActualizar(a); }
    public List<Aceite> obtenerAceites() { return dao.obtenerTodos(Aceite.class); }

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
}
