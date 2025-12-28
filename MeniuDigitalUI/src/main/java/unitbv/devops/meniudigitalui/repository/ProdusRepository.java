package unitbv.devops.meniudigitalui.repository;

import unitbv.devops.meniudigitalui.entity.Produs;
import org.hibernate.Session;
import unitbv.devops.meniudigitalui.db.HibernateUtil;

import java.util.List;
import java.util.Optional;

public class ProdusRepository {

    public Produs save(Produs produs) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.persist(produs);
            session.getTransaction().commit();
            return produs;
        }
    }

    public Optional<Produs> findById(Integer id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.find(Produs.class, id));
        }
    }

    public List<Produs> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return session.createQuery("FROM Produs ORDER BY nume", Produs.class).list();
        }
    }

    public List<Produs> findByNume(String nume) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Produs WHERE LOWER(nume) LIKE LOWER(:nume)", Produs.class)
                    .setParameter("nume", "%" + nume + "%")
                    .list();
        }
    }

    public void delete(Integer id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Produs produs = session.find(Produs.class, id);
            if (produs != null) {
                session.remove(produs);
            }
            session.getTransaction().commit();
        }
    }

    public void update(Produs produs) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.merge(produs);
            session.getTransaction().commit();
        }
    }
}
