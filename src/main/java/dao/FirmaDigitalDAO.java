package dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import model.FirmaDigital;

public class FirmaDigitalDAO {
    private EntityManagerFactory emf;

    public FirmaDigitalDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public boolean guardarFirma(FirmaDigital firma) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        boolean guardado = false;

        try {
            tx.begin();
            em.persist(firma);
            tx.commit();
            guardado = true;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }

        return guardado;
    }
}
