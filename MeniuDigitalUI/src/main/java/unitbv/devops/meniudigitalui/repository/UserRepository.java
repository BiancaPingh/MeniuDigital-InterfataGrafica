package unitbv.devops.meniudigitalui.repository;

import org.hibernate.Session;
import unitbv.devops.meniudigitalui.db.HibernateUtil;
import unitbv.devops.meniudigitalui.entity.User;
import unitbv.devops.meniudigitalui.entity.UserRole;

import java.util.List;
import java.util.Optional;

public class UserRepository {

    public User save(User user) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.persist(user);
            session.getTransaction().commit();
            return user;
        }
    }

    public Optional<User> findById(Integer id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.find(User.class, id));
        }
    }

    public Optional<User> findByUsername(String username) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM User WHERE username = :username", User.class)
                    .setParameter("username", username)
                    .uniqueResultOptional();
        }
    }

    public List<User> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM User ORDER BY username", User.class).list();
        }
    }

    public List<User> findByRole(UserRole role) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM User WHERE role = :role ORDER BY username", User.class)
                    .setParameter("role", role)
                    .list();
        }
    }

    public void delete(Integer id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            User user = session.find(User.class, id);
            if (user != null) {
                session.remove(user);
            }
            session.getTransaction().commit();
        }
    }

    public void update(User user) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.merge(user);
            session.getTransaction().commit();
        }
    }
}
