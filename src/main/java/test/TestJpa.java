package test;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class TestJpa {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("sqlserver");
        System.out.println("EMF creado exitosamente: " + emf);
        emf.close();
    }
}
