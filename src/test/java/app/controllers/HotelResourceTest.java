package app.controllers;

import app.config.HibernateConfig;
import app.entities.Hotel;
import app.entities.Room;
import app.config.ApplicationConfig;
import app.rest.Routes;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class HotelResourceTest
{
    private static final EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryForTest();
    ObjectMapper objectMapper = new ObjectMapper();
    Hotel hotel;
    Hotel hotel2;


    @BeforeAll
    static void setupAll()
    {
        ApplicationConfig
            .getInstance()
            .initiateServer()
            .setRoute(Routes.getRoutes())
            .handleExceptions()
            .startServer(7777);
        RestAssured.baseURI = "http://localhost:7777/api";
    }

    @BeforeEach
    void setUp()
    {
        try (EntityManager em = emf.createEntityManager())
        {
            em.getTransaction().begin();

            // wipe db
            em.createQuery("DELETE FROM Room").executeUpdate();
            em.createQuery("DELETE FROM Hotel").executeUpdate();
            em.createNativeQuery("ALTER SEQUENCE room_id_seq RESTART WITH 1");
            em.createNativeQuery("ALTER SEQUENCE hotel_id_seq RESTART WITH 1");

            // new entities
            hotel = new Hotel();
            hotel.setName("Grand Deluxe Hotel");
            em.persist(hotel);

            hotel2 = new Hotel();
            hotel2.setName("Miami Beach Hotel");
            em.persist(hotel2);

            Room room = new Room();
            room.setHotel(hotel);
            em.persist(room);

            Room room2 = new Room();
            room2.setHotel(hotel2);
            em.persist(room2);
            //Set<Room> rooms = new HashSet<>(Set.of(room, room2));

            // setters
           // hotel.setRooms(rooms);

            // save to db
            em.getTransaction().commit();

            em.clear();

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @AfterAll
    static void tearDown()
    {
        if (emf != null && emf.isOpen())
        {
            emf.close();
            System.out.println("EMF is closed....");
        }
        ApplicationConfig.getInstance().stopServer();
    }

    @Test
    @DisplayName("Test : create hotel")
    void testCreate()
    {
        Hotel hotel3 = new Hotel("Black Hotel");
        try
        {
            String json = objectMapper.writeValueAsString(hotel3);

            given().when()
                .contentType("application/json")
                .accept("application/json")
                .body(json)
                .post("/hotel")
                .then()
                .statusCode(200)
                .body("name", equalTo(hotel3.getName()));
        } catch (JsonProcessingException e)
        {
            e.printStackTrace();
        }

    }

    @Test
    @DisplayName("Test : update hotel")
    void testUpdate()
    {
        hotel.setName("CPhbusiness Hotel");
        try
        {
            given().when()
                .contentType("application/json")
                .accept("application/json")
                .body(hotel)
                .put("/hotel/{id}", hotel.getId())
                .then()
                .statusCode(200)
                .body("name", equalTo(hotel.getName()));
        } catch (Exception e)
        {
            e.printStackTrace();
        }


    }

    @Test
    @DisplayName("Test : read hotel")
    void read()
    {
    }

    @Test
    @DisplayName("Test : find all hotels")
    void findAll()
    {
    }

    @Test
    @DisplayName("Test : delete hotel")
    void testDelete()
    {
        try
        {
            given().when()
                .body(hotel2)
                .delete("/hotel/{id}", hotel2.getId())
                .then()
                .statusCode(200);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Test : get hotel by id")
    void testGetById()
    {
    }
}
