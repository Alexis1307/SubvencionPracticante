package dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import model.Usuario;

public class UsuarioDAO {
	private EntityManagerFactory emf = Persistence.createEntityManagerFactory("sqlserver");
	
	public UsuarioDAO(EntityManagerFactory emf) {
		this.emf = emf;
	}
	
	public Usuario buscarPorNombre(String nombreUsuario) {
	    EntityManager em = emf.createEntityManager();
	    Usuario usuario = null;

	    try {
	        TypedQuery<Usuario> query = em.createQuery(
	            "SELECT u FROM Usuario u JOIN FETCH u.rol WHERE u.nombreUsuario = :nombre", Usuario.class);
	        query.setParameter("nombre", nombreUsuario);
	        usuario = query.getSingleResult();
	    } catch (NoResultException e) {
	        System.out.println("Usuario no encontrado.");
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        em.close(); 
	    }

	    return usuario;
	}
	
	public Usuario validarCredenciales(String nombreUsuario, String contrasena) {
	    try {
	    	EntityManager em = emf.createEntityManager(); 

	        TypedQuery<Usuario> query = em.createQuery(
	            "SELECT u FROM Usuario u WHERE u.nombreUsuario = :usuario AND u.contrasena = :pass", Usuario.class);
	        query.setParameter("usuario", nombreUsuario);
	        query.setParameter("pass", contrasena);
	        return query.getSingleResult();
	    } catch (NoResultException e) {
	        return null;
	    }
	}

	public Usuario obtenerJefeUnidad() {
	    EntityManager em = emf.createEntityManager();
	    try {
	        return em.createQuery(
	            "SELECT u FROM Usuario u WHERE LOWER(u.nombreUsuario) = :nombre", Usuario.class)
	            .setParameter("nombre", "jefeunidad") 
	            .getSingleResult();
	    } catch (NoResultException e) {
	        System.out.println("No se encontró el jefe de unidad.");
	        return null;
	    } finally {
	        em.close();
	    }
	}
}
