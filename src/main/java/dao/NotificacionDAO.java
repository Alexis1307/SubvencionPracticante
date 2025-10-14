package dao;

import jakarta.persistence.*;
import model.Notificacion;

public class NotificacionDAO {
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("sqlserver");

    public void crearNotificacion(Notificacion notificacion) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            em.persist(notificacion);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}
