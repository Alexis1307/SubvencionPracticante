package dao;

import java.util.List;

import jakarta.persistence.*;
import model.Informe;
import model.Notificacion;

public class NotificacionDAO {
    private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("sqlserver");

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
    
    public static List<Notificacion> obtenerPorUsuario(int usuarioId) {
        EntityManager em = emf.createEntityManager();
        try {
            String jpql = "SELECT n FROM Notificacion n WHERE n.usuario.usuarioId = :usuarioId ORDER BY n.fecha DESC";
            TypedQuery<Notificacion> query = em.createQuery(jpql, Notificacion.class);
            query.setParameter("usuarioId", usuarioId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}
