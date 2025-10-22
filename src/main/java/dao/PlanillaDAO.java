package dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import model.Planilla;
import util.JpaUtil;

public class PlanillaDAO {
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("sqlserver");

    public void guardar(Planilla planilla) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(planilla);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
        
        System.out.println("Planilla guardada en BD: " + planilla.getNombrePlanilla());

    }
}
