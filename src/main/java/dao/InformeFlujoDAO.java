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
}
