package app.config;

import app.entities.*;
import app.entities.Role;
import app.entities.User;
import app.utils.Utils;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import java.util.Properties;

public class HibernateConfig {
    private static EntityManagerFactory emf;
    private static EntityManagerFactory emfTest;
    private static Boolean isTest = false;

    public static void setTest(Boolean test) {
        isTest = test;
    }

    public static Boolean getTest() {
        return isTest;
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        if (System.getenv("PRODUCTION") != null) {
            return setupHibernateConfigurationForProduction();
        }
        return isTest ? getEntityManagerFactoryForTest() : getEntityManagerFactoryForDevelopment();
    }

    public static EntityManagerFactory getEntityManagerFactoryForTest() {
        if (emfTest == null) {
            setTest(true);
            emfTest = createEMF(true);  // No DB needed for test
        }
        return emfTest;
    }

    public static EntityManagerFactory getEntityManagerFactoryForDevelopment() {
        if (emf == null) {
            emf = createEMF(false);
        }
        return emf;
    }

    private static EntityManagerFactory setupHibernateConfigurationForProduction() {
        Properties props = new Properties();
        // Get values from environment variables
        props.put("hibernate.connection.url", System.getenv("JDBC_DATABASE_URL"));
        props.put("hibernate.connection.username", System.getenv("JDBC_DATABASE_USERNAME"));
        props.put("hibernate.connection.password", System.getenv("JDBC_DATABASE_PASSWORD"));
        props.put("hibernate.hbm2ddl.auto", "update"); // Use update in production for schema updates
        return new Configuration().addProperties(props).addAnnotatedClass(Hotel.class)
            .addAnnotatedClass(Room.class)
            .addAnnotatedClass(User.class)
            .addAnnotatedClass(Role.class)
            .buildSessionFactory();
    }

    private static EntityManagerFactory createEMF(boolean forTest) {
        try {
            Configuration configuration = new Configuration();
            Properties props = new Properties();
            // Set the base properties
            setBaseProperties(props);
            if (forTest) {
                props = setTestProperties(props);
            } else if (System.getenv("PRODUCTION") != null) {
                props = setDeployedProperties(props);
            } else {
                props = setDevProperties(props);
            }
            configuration.setProperties(props);
            getAnnotationConfiguration(configuration);

            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties())
                .build();
            SessionFactory sf = configuration.buildSessionFactory(serviceRegistry);
            EntityManagerFactory emf = sf.unwrap(EntityManagerFactory.class);
            return emf;
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    private static void getAnnotationConfiguration(Configuration configuration) {
        configuration.addAnnotatedClass(Hotel.class);
        configuration.addAnnotatedClass(Room.class);
        configuration.addAnnotatedClass(User.class);
        configuration.addAnnotatedClass(Role.class);
    }

    private static Properties setBaseProperties(Properties props) {
        props.put("hibernate.connection.driver_class", "org.postgresql.Driver");
        props.put("hibernate.hbm2ddl.auto", "create");  // set to "update" when in production
        props.put("hibernate.current_session_context_class", "thread");
        props.put("hibernate.show_sql", "false");
        props.put("hibernate.format_sql", "false");
        props.put("hibernate.use_sql_comments", "false");
        return props;
    }

    private static Properties setDeployedProperties(Properties props) {
        String DBName = System.getenv("DB_NAME");
        props.setProperty("hibernate.connection.url", System.getenv("CONNECTION_STR") + DBName);
        props.setProperty("hibernate.connection.username", System.getenv("DB_USERNAME"));
        props.setProperty("hibernate.connection.password", System.getenv("DB_PASSWORD"));
        return props;
    }

    private static Properties setDevProperties(Properties props) {
        String DBName = Utils.getPropertyValue("DB_NAME", "config.properties");
        String DB_USERNAME = Utils.getPropertyValue("DB_USERNAME", "config.properties");
        String DB_PASSWORD = Utils.getPropertyValue("DB_PASSWORD", "config.properties");
        props.put("hibernate.connection.url", "jdbc:postgresql://localhost:5432/" + DBName);
        props.put("hibernate.connection.username", DB_USERNAME);
        props.put("hibernate.connection.password", DB_PASSWORD);
        return props;
    }
    private static Properties setTestProperties(Properties props) {

        props.put("hibernate.connection.driver_class", "org.testcontainers.jdbc.ContainerDatabaseDriver");
        props.put("hibernate.connection.url", "jdbc:tc:postgresql:16.2:///test_db");
        props.put("hibernate.archive.autodetection", "hbm,class");
        props.put("hibernate.show_sql", "false");
        props.put("hibernate.hbm2ddl.auto", "create-drop");
        return props;
    }
}
