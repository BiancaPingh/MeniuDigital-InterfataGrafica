package unitbv.devops.meniudigitalui.repository;

import unitbv.devops.meniudigitalui.entity.Comanda;
import org.hibernate.Session;
import unitbv.devops.meniudigitalui.db.HibernateUtil;

import java.util.List;
import java.util.Optional;

public class ComandaRepository {

    public Comanda save(Comanda comanda) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.persist(comanda);
            session.getTransaction().commit();
            return comanda;
        }
    }

    public Optional<Comanda> findById(Integer id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.find(Comanda.class, id));
        }
    }

    public List<Comanda> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Comanda ORDER BY dataOra DESC", Comanda.class).list();
        }
    }

    public List<Comanda> findAllWithUser() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return session.createQuery(
                            "SELECT c FROM Comanda c " +
                            "JOIN FETCH c.user " +
                            "LEFT JOIN FETCH c.masa " +
                            "ORDER BY c.dataOra DESC",
                            Comanda.class)
                    .list();
        }
    }

    public List<Comanda> findByUserId(Integer userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Comanda WHERE user.id = :userId ORDER BY dataOra DESC", Comanda.class)
                    .setParameter("userId", userId)
                    .list();
        }
    }

    public List<Comanda> findByUserIdWithUser(Integer userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return session.createQuery(
                            "SELECT c FROM Comanda c " +
                            "JOIN FETCH c.user " +
                            "LEFT JOIN FETCH c.masa " +
                            "WHERE c.user.id = :userId ORDER BY c.dataOra DESC",
                            Comanda.class)
                    .setParameter("userId", userId)
                    .list();
        }
    }

    public void delete(Integer id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Comanda comanda = session.find(Comanda.class, id);
            if (comanda != null) {
                session.remove(comanda);
            }
            session.getTransaction().commit();
        }
    }

    public void update(Comanda comanda) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.merge(comanda);
            session.getTransaction().commit();
        }
    }
}
