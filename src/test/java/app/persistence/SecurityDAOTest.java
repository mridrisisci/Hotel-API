package app.persistence;

import app.config.HibernateConfig;
import app.daos.UserDAO;
import app.exceptions.ApiException;
import app.exceptions.ValidationException;
import app.entities.Role;
import app.entities.User;
import dk.bugelhartmann.UserDTO;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.*;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SecurityDAOTest {
    private static final EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryForTest();
    private static final UserDAO securityDAO = UserDAO.getInstance(emf);
    private static User testUser;
    private static Role userRole, adminRole;

    @BeforeEach
    void setUp() {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            // Clean up existing data
            em.createQuery("DELETE FROM User").executeUpdate();
            em.createQuery("DELETE FROM Role").executeUpdate();

            // Create test roles
            userRole = new Role("user");
            adminRole = new Role("admin");
            em.persist(userRole);
            em.persist(adminRole);

            // Create test user with user role
            testUser = new User("testuser", "password123");
            testUser.addRole(userRole);
            em.persist(testUser);

            em.getTransaction().commit();
        }
    }

    /*@AfterAll
    void tearDown() {
        if (emf != null && emf.isOpen()) {
            emf.close();
            System.out.println("EntityManagerFactory closed");
        }
    }*/

    @Test
    void testGetVerifiedUser_Success() throws ValidationException {
        // Arrange
        String username = "testuser";
        String password = "password123";

        // Act
        UserDTO result = securityDAO.getVerifiedUser(username, password);

        // Assert
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertTrue(result.getRoles().contains("user"));
        assertEquals(1, result.getRoles().size());
    }

    @Test
    void testGetVerifiedUser_WrongPassword() {
        // Arrange
        String username = "testuser";
        String wrongPassword = "wrongpassword";

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            securityDAO.getVerifiedUser(username, wrongPassword);
        });

        assertEquals("Password does not match", exception.getMessage());
    }

    @Test
    void testGetVerifiedUser_UserNotFound() {
        // Arrange
        String nonExistentUsername = "nonexistentuser";
        String password = "password123";

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            securityDAO.getVerifiedUser(nonExistentUsername, password);
        });

        assertTrue(exception.getMessage().contains("User not found"));
    }

    @Test
    void testCreateUser_Success() {
        // Arrange
        User user = new User("newuser", "newpassword");

        // Act
        User result = securityDAO.create(user);

        // Assert
        assertNotNull(result);
        assertEquals(user.getUsername(), result.getUsername());

        // Verify user was persisted with the user role
        try (EntityManager em = emf.createEntityManager()) {
            User persistedUser = em.find(User.class, user.getUsername());
            assertNotNull(persistedUser);
            assertEquals(1, persistedUser.getRoles().size());
            assertTrue(persistedUser.getRolesAsStrings().contains("user"));
        }
    }

    @Test
    void testCreateUser_UserAlreadyExists() {
        // Arrange
        User user = new User("testuser", "newpassword");

        // Act & Assert
        ApiException exception = assertThrows(ApiException.class, () -> {
            securityDAO.create(user);
        });

        assertEquals(400, exception.getCode());
        assertTrue(exception.getMessage().contains("Error creating user"));
    }

    @Test
    void testAddRoleToUser_Success() {
        // Arrange
        String username = testUser.getUsername();
        adminRole = new Role("admin");
        //String roleName = adminRole.getRoleName();

        // Act
        User result = testUser.addRole(adminRole); /////////////////////////////

        // Assert
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals(2, result.getRoles().size());
        assertTrue(result.getRolesAsStrings().contains("user"));
        assertTrue(result.getRolesAsStrings().contains("admin"));

        // Verify role was added in the database
        try (EntityManager em = emf.createEntityManager()) {
            User persistedUser = em.find(User.class, username);
            assertNotNull(persistedUser);
            assertEquals(2, persistedUser.getRoles().size());
            assertTrue(persistedUser.getRolesAsStrings().contains("admin"));
        }
    }

    @Test
    void testAddRoleToUser_UserNotFound() {
        // Arrange
        String nonExistentUsername = "nonexistentuser";
        adminRole = new Role("admin");
        //String roleName = "admin";

        // Act & Assert
        ApiException exception = assertThrows(ApiException.class, () -> {
            testUser.addRole(adminRole);
        });

        assertEquals(400, exception.getCode());
        assertTrue(exception.getMessage().contains("Error adding role to user"));
    }

    @Test
    void testAddRoleToUser_RoleNotFound() {
        // Arrange
        String username = "testuser";
        adminRole = new Role("admin");
        //String nonExistentRole = "nonexistentrole";

        // Act & Assert
        ApiException exception = assertThrows(ApiException.class, () -> {
            testUser.addRole(adminRole);
        });

        assertEquals(400, exception.getCode());
        assertTrue(exception.getMessage().contains("Error adding role to user"));
    }

    @Test
    void testRemoveRoleFromUser_Success() {
        // First add admin role to test user
        adminRole = new Role("admin");
        testUser.addRole(adminRole);

        // Arrange
        String username = "testuser";
        //String roleName = "admin";

        // Act
        User result = testUser.removeRole(adminRole.toString());

        // Assert
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals(1, result.getRoles().size());
        assertTrue(result.getRolesAsStrings().contains("user"));
        assertFalse(result.getRolesAsStrings().contains("admin"));

        // Verify role was removed in the database
        try (EntityManager em = emf.createEntityManager()) {
            User persistedUser = em.find(User.class, username);
            assertNotNull(persistedUser);
            assertEquals(1, persistedUser.getRoles().size());
            assertFalse(persistedUser.getRolesAsStrings().contains("admin"));
        }
    }

    @Test
    void testRemoveRoleFromUser_UserNotFound() {
        // Arrange
        String nonExistentUsername = "nonexistentuser";
        //String roleName = "user";

        // Act & Assert
        ApiException exception = assertThrows(ApiException.class, () -> {
            testUser.removeRole(adminRole.toString());
        });

        assertEquals(400, exception.getCode());
        assertTrue(exception.getMessage().contains("Error removing role from user"));
    }

    @Test
    void testRemoveRoleFromUser_RoleNotFound() {
        // Arrange
        String username = "testuser";
        //String nonExistentRole = "nonexistentrole";

        // Act & Assert
        ApiException exception = assertThrows(ApiException.class, () -> {
            testUser.removeRole(adminRole.toString());
        });

        assertEquals(400, exception.getCode());
        assertTrue(exception.getMessage().contains("Error removing role from user"));
    }
}
