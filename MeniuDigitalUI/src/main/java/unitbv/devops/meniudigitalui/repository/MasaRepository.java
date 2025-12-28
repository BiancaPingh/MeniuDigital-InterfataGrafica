package unitbv.devops.meniudigitalui.repository;

import org.hibernate.Session;
import unitbv.devops.meniudigitalui.db.HibernateUtil;
import unitbv.devops.meniudigitalui.entity.Masa;

import java.util.List;
import java.util.Optional;

public class MasaRepository {

    public Masa save(Masa masa) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.persist(masa);
            session.getTransaction().commit();
            return masa;
        }
    }

    public Optional<Masa> findById(Integer id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.find(Masa.class, id));
        }
    }

    public Optional<Masa> findByNumar(Integer numar) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Masa WHERE numar = :numar", Masa.class)
                    .setParameter("numar", numar)
                    .uniqueResultOptional();
        }
    }

    public List<Masa> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Masa ORDER BY numar", Masa.class).list();
        }
    }

    public void update(Masa masa) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.merge(masa);
            session.getTransaction().commit();
        }
    }
}

