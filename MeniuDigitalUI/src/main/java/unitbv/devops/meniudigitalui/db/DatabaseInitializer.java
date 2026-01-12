package unitbv.devops.meniudigitalui.db;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import unitbv.devops.meniudigitalui.entity.*;

public class DatabaseInitializer {

    public static void initializeDatabase(SessionFactory sessionFactory) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            initializeUsers(session);
            initializeTables(session);
            initializeMenu(session);

            session.getTransaction().commit();
        }

        // safety: print counts and reseed if needed
        try (Session s = sessionFactory.openSession()) {
            Long produse = s.createQuery("select count(p) from Produs p", Long.class).getSingleResult();
            System.out.println("[INIT] produse after init = " + produse);
            if (produse == 0) {
                System.out.println("[INIT] No products found after init, re-seeding menu");
                s.beginTransaction();
                initializeMenu(s);
                s.getTransaction().commit();
            }
        }
    }

    private static void initializeUsers(Session session) {
        long userCount = session.createQuery("SELECT COUNT(u) FROM User u", Long.class).getSingleResult();
        if (userCount == 0) {
            session.persist(new User("admin", "admin123", UserRole.ADMIN));
            session.persist(new User("waiter1", "pass123", UserRole.STAFF));
            session.persist(new User("waiter2", "pass123", UserRole.STAFF));
            session.persist(new User("guest", "guest", UserRole.GUEST));
        } else {
            // Ensure guest exists even if DB was already initialized
            Long guestCount = session.createQuery("SELECT COUNT(u) FROM User u WHERE u.username = :u", Long.class)
                    .setParameter("u", "guest")
                    .getSingleResult();
            if (guestCount == 0) {
                session.persist(new User("guest", "guest", UserRole.GUEST));
                System.out.println("[INIT] Admin/Staff existed, but Guest was missing. Created user 'guest'.");
            }
        }
    }

    private static void initializeTables(Session session) {
        long tableCount = session.createQuery("SELECT COUNT(m) FROM Masa m", Long.class).getSingleResult();
        if (tableCount == 0) {
            for (int i = 1; i <= 12; i++) {
                session.persist(new Masa(i));
            }
        }
    }

    private static void initializeMenu(Session session) {
        long produsCount = session.createQuery("SELECT COUNT(p) FROM Produs p", Long.class).getSingleResult();
        if (produsCount == 0) {
            // Menu inspired by src/main/meniuConsola/Main.java
            session.persist(new Mancare("Pizza Margherita", 45.0, "", true));
            session.persist(new Mancare("Pizza Quattro Formaggi", 48.0, "", true));
            session.persist(new Mancare("Pizza", 45.0, "", false));
            session.persist(new Mancare("Paste Carbonara", 52.5, "", false));
            session.persist(new Mancare("Paste Vegetariene", 48.0, "", true));
            session.persist(new Mancare("Salata de vinete", 18.0, "", true));
            session.persist(new Mancare("Hummus", 22.0, "", true));
            session.persist(new Mancare("Bruschette", 25.0, "", true));
            session.persist(new Mancare("Tiramisu", 25.0, "", true));
            session.persist(new Mancare("Cheesecake", 28.0, "", true));
            session.persist(new Mancare("Salata de fructe", 20.0, "", true));
            session.persist(new Mancare("Risotto cu ciuperci", 55.0, "", true));
            session.persist(new Mancare("Friptura de vita", 120.0, "", false));

            session.persist(new Bauturi("Limonada", 15.0, "", true));
            session.persist(new Bauturi("Apa", 8.0, "", true));
            session.persist(new Bauturi("Vin Rosu", 25.0, "", true));
            session.persist(new Bauturi("Bere", 12.0, "", true));
        }
    }
}
