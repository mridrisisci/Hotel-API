package app.daos;

import app.config.HibernateConfig;
import app.entities.Role;
import app.entities.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

public class UserDAO
{
    private GenericDAO genericDAO;
    private static EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
    private static UserDAO instance;

    private UserDAO(EntityManagerFactory emf)
    {
        genericDAO = GenericDAO.getInstance(emf);

    }

    public static UserDAO getInstance(EntityManagerFactory emf)
    {
        if (instance == null)
        {
            instance = new UserDAO(emf);
        }
        return instance;
    }

    public User create(User user)
    {
        try (EntityManager em = emf.createEntityManager())
        {
            if (user.getRoles().size() == 0)
            {
                Role userRole = em.find(Role.class, "user");
                if (userRole == null)
                {
                    userRole = new Role("user");
                    em.persist(userRole);
                }
                user.addRole(userRole);
            }
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
}
