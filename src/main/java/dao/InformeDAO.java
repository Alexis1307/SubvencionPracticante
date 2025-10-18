package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
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
	        return em.createQuery(
	            "SELECT i FROM Informe i WHERE i.practicante.usuarioId = :id", Informe.class)
	            .setParameter("id", practicanteId)
	            .getResultList();
	    } catch (Exception e) {
	        e.printStackTrace();
	        return new ArrayList<>();
	    } finally {
	        em.close();
	    }
	}
	
	public List<Informe> obtenerInformesPendientes() {
	    EntityManager em = emf.createEntityManager();
	    try {
	        return em.createQuery(
	                "SELECT i FROM Informe i WHERE i.estado = 'Pendiente' OR i.estado = 'En revisión'",
	                Informe.class)
	                .getResultList();
	    } catch (Exception e) {
	        throw new RuntimeException("Error al obtener informes pendientes", e);
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
	
	 public void actualizarRutaInforme(int informeId, String nuevaRuta) {
	        EntityManager em = emf.createEntityManager();
	        EntityTransaction tx = em.getTransaction();

	        try {
	            tx.begin();

	            Informe informe = em.find(Informe.class, informeId);
	            if (informe != null) {
	                informe.setRutaDocumento(nuevaRuta);
	                em.merge(informe); // No es obligatorio aquí, pero es seguro
	            }

	            tx.commit();
	        } catch (Exception e) {
	            if (tx.isActive()) {
	                tx.rollback();
	            }
	            e.printStackTrace();
	        } finally {
	            em.close();
	        }
	    }
	 
	 public Informe obtenerInformePorId(int id) {
		    EntityManager em = emf.createEntityManager();
		    try {
		        return em.find(Informe.class, id);
		    } finally {
		        em.close();
		    }
	 }
	 
	 public List<Informe> obtenerInformesPorEstado(String estado) {
		    EntityManager em = emf.createEntityManager();
	        String jpql = "SELECT i FROM Informe i WHERE i.estado = :estado ORDER BY i.fechaEnvio DESC";
	        TypedQuery<Informe> query = em.createQuery(jpql, Informe.class);
	        query.setParameter("estado", estado);
	        return query.getResultList();
	    }
}
