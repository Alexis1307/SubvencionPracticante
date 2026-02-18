package dao;

import jakarta.persistence.*;
import model.InformeFlujo;

public class InformeFlujoDAO {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("sqlserver");

    public void registrarFlujo(InformeFlujo flujo) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            em.persist(flujo);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Error al registrar flujo del informe", e);
        } finally {
            em.close();
        }
    }
    
    public boolean actualizarEstadoFlujoPorInforme(int informeId, String nuevoEstado) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        boolean actualizado = false;
        try {
            tx.begin();
            TypedQuery<InformeFlujo> query = em.createQuery(
                "SELECT f FROM InformeFlujo f WHERE f.informe.informeID = :id ORDER BY f.fecha DESC",
                InformeFlujo.class);
            query.setParameter("id", informeId);
            query.setMaxResults(1);
            InformeFlujo flujo = query.getSingleResult();

            if (flujo != null) {
                flujo.setEstado(nuevoEstado);
                em.merge(flujo);
                actualizado = true;
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
        return actualizado;
    }

}
