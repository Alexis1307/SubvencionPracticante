package dao;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import model.Rol;

public class RolDAO {
	private EntityManagerFactory emf = Persistence.createEntityManagerFactory("sqlserver");

    
	
	public RolDAO(EntityManagerFactory emf) {
		super();
		this.emf = emf;
	}
	
	public RolDAO() {
		
	}

	public String obtenerNombreRol(int rolId) {
        EntityManager em = emf.createEntityManager();
        try {
            Rol rol = em.find(Rol.class, rolId);
            return (rol != null) ? rol.getNombreRol() : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }


    public List<Rol> obtenerTodos() {
        EntityManager em = emf.createEntityManager();
        
        TypedQuery<Rol> query = em.createQuery("SELECT r FROM Rol r", Rol.class);
        return query.getResultList();
    }
    
    public Rol obtenerPorId(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Rol.class, id);
        } finally {
            em.close();
        }
    }
}
