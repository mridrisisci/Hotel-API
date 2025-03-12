package app.security.daos;

import app.config.HibernateConfig;
import app.security.entities.Role;
import app.security.entities.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserDAO
{
    static Logger logger = LoggerFactory.getLogger(UserDAO.class);

    EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();

    public User create(User user)
    {
        try (EntityManager em = emf.createEntityManager())
        {
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();
            return user;
        } catch (Exception e)
        {
            logger.error("Error creating user: " + e.getMessage());
            return null;
        }
    }

    public Role createRole(Role role)
    {
        try (EntityManager em = emf.createEntityManager())
        {
            em.getTransaction().begin();
            em.persist(role);
            em.getTransaction().commit();
            return role;
        }

    }
        public static void main(String[] args)
        {
            UserDAO dao = new UserDAO();

            User user = new User("admin", "kodeord");
            User admin = new User("Jesper", "admin");

            // creating new roles
            Role adminRole = new Role("admin");
            Role userRole = new Role("user");



            // persist roles
            dao.createRole(adminRole);
            dao.createRole(userRole);
            user.addRole(adminRole);

            // persist user
            User createdUser = dao.create(user);



            boolean result = createdUser.verifyPassword("kodeord");
            System.out.println(result);
            logger.info("User created: " + createdUser.getUsername());
        }
    }
