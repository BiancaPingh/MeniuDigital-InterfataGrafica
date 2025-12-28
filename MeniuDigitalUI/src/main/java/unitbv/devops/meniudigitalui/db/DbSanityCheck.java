package unitbv.devops.meniudigitalui.db;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

public final class DbSanityCheck {
    private DbSanityCheck() {
    }

    public static void printCounts(SessionFactory sf) {
        try (Session s = sf.openSession()) {
            Long produse = s.createQuery("select count(p) from Produs p", Long.class).getSingleResult();
            Long mese = s.createQuery("select count(m) from Masa m", Long.class).getSingleResult();
            Long users = s.createQuery("select count(u) from User u", Long.class).getSingleResult();
            System.out.println("[DB] users=" + users + ", mese=" + mese + ", produse=" + produse);
        }
    }
}

