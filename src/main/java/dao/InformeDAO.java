package dao;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import model.Informe;

public class InformeDAO {
	private EntityManagerFactory emf = Persistence.createEntityManagerFactory("sqlserver");
	
	public boolean guardarInforme(Informe informe) {
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = null;
		boolean guardado = false;
		
		try {
			tx = em.getTransaction();
			tx.begin();
			
			em.persist(informe);
			
			tx.commit();
			guardado = true;
		} catch (Exception e) {
			if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
		
		return guardado;
	}
	
	public List<Informe> obtenerInformes(int practicanteId) {
	    EntityManager em = emf.createEntityManager();
	    try {
	        return em.createQuery("SELECT i FROM Informe i WHERE i.practicanteId = :id", Informe.class)
	                 .setParameter("id", practicanteId)
	                 .getResultList();
	    }catch (Exception e) {
	        e.printStackTrace();
	        return new ArrayList<>();
	    }  finally {
	        em.close();
	    }
	}
	
	public List<Informe> obtenerInformesPendientes() {
	    EntityManager em = emf.createEntityManager();
	    try {
	        return em.createQuery(
	                "SELECT i FROM Informe i WHERE i.estadoInforme = 'Pendiente' OR i.estadoInforme = 'En revisión'",
	                Informe.class)
	                .getResultList();
	    } catch (Exception e) {
	        e.printStackTrace();
	        return new ArrayList<>();
	    } finally {
	        em.close();
	    }
	}
	
	public boolean actualizarEstadoInforme(int informeId, String nuevoEstado) {
	    EntityManager em = emf.createEntityManager();
	    EntityTransaction tx = null;
	    boolean actualizado = false;

	    try {
	        tx = em.getTransaction();
	        tx.begin();

	        Informe informe = em.find(Informe.class, informeId);
	        if (informe != null) {
	            informe.setEstado(nuevoEstado);
	            em.merge(informe);
	            actualizado = true;
	        }

	        tx.commit();
	    } catch (Exception e) {
	        if (tx != null && tx.isActive()) {
	            tx.rollback();
	        }
	        e.printStackTrace();
	    } finally {
	        em.close();
	    }

	    return actualizado;
	}
}
