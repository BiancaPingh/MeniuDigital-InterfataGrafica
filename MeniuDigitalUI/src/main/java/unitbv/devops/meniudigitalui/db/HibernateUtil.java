package unitbv.devops.meniudigitalui.db;

import jakarta.persistence.Persistence;
import org.hibernate.SessionFactory;

public class HibernateUtil {
    private static SessionFactory sessionFactory;

    public static void initialize() {
        try {
            var emf = Persistence.createEntityManagerFactory("RestaurantPU");
            sessionFactory = emf.unwrap(SessionFactory.class);
        } catch (Throwable ex) {
            System.err.println("Failed to initialize Hibernate: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            initialize();
        }
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}

