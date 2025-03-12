package app.controllers;

import app.config.HibernateConfig;
import app.daos.GenericDAO;
import app.entities.Hotel;
import app.entities.Room;
import app.rest.ApplicationConfig;
import app.rest.Routes;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
        ApplicationConfig.getInstance()
            .initiateServer()
            .setRoute(Routes.getRoutes())
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
            em.createQuery("DELETE FROM Hotel").executeUpdate();
            em.createQuery("DELETE FROM Room").executeUpdate();

            // new entities
            hotel = new Hotel();
            hotel2 = new Hotel();
            Room room = new Room();
            Room room2 = new Room();
            Set<Room> rooms = new HashSet<>(Set.of(room, room2));

            // setters
            hotel.setName("Grand Deluxe Hotel");
            hotel2.setName("Miami Beach Hotel");
            hotel.setRooms(rooms);

            // save to db
            em.persist(hotel);
            em.persist(hotel2);
            em.persist(room);
            em.persist(room2);
            em.persist(rooms);
            em.getTransaction().commit();

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @AfterEach
    void tearDown()
    {
        ApplicationConfig.stopServer();
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
        given().when().get("/").then().statusCode(200);
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
                .statusCode(202);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Test : get hotel by id")
    void testGetById()
    {
        given().when().get("/hotel/1").then().statusCode(200).body("id", equalTo(1));
    }
}
