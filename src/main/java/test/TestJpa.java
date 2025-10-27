package test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class TestJpa {
    public static void main(String[] args) {
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("sqlserver");
            EntityManager em = emf.createEntityManager();
            System.out.println("✅ Conexión exitosa a la base de datos remota!");
            em.close();
            emf.close();
        } catch (Exception e) {
            System.out.println("❌ Error al conectar: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
