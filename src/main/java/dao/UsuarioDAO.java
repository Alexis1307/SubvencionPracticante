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
}
