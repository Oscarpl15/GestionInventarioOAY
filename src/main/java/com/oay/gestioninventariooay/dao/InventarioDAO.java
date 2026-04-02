package com.oay.gestioninventariooay.dao;

import com.oay.gestioninventariooay.util.GestorBBDD;
import jakarta.persistence.EntityManager;
import java.util.List;

public class InventarioDAO {

    public <T> void guardarOActualizar(T entidad) throws Exception {
        EntityManager em = null;
        try {
            em = GestorBBDD.getEntityManagerFactory().createEntityManager();
            em.getTransaction().begin();
            em.merge(entidad); // merge sirve tanto para insertar nuevo como actualizar existente
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) em.getTransaction().rollback();
            throw new Exception("Error al guardar en BBDD: " + e.getMessage());
        } finally {
            if (em != null) em.close();
        }
    }

    public <T> List<T> obtenerTodos(Class<T> clase) {
        try (EntityManager em = GestorBBDD.getEntityManagerFactory().createEntityManager()) {
            return em.createQuery("SELECT e FROM " + clase.getSimpleName() + " e", clase).getResultList();
        } catch (Exception e) {
            System.err.println("Error al cargar " + clase.getSimpleName() + ": " + e.getMessage());
            return List.of();
        }
    }
}
