package app.controllers;

import app.config.ApplicationConfig;
import app.config.HibernateConfig;
import app.dtos.HotelDTO;
import app.entities.Hotel;
import app.entities.Room;
import app.rest.Routes;
import app.security.controllers.SecurityController;
import io.restassured.RestAssured;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.jupiter.api.Assertions.fail;

public class HotelResourceTest
{
    private static final EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryForTest();
    final ObjectMapper objectMapper = new ObjectMapper();
    private final Logger logger = LoggerFactory.getLogger(HotelResourceTest.class.getName());
    private static SecurityController securityController;
    Hotel hotelTest1, hotelTest2;

    @BeforeAll
    static void beforeAll()
    {
        HotelController hotelController = new HotelController(emf);
        securityController = new SecurityController();
        Routes routes = new Routes(hotelController, securityController);
        ApplicationConfig
            .getInstance()
            .initiateServer()
            .setRoute(routes.getRoutes())
            .handleExceptions()
            .checkSecurityRoles()
            .startServer(7000);
        RestAssured.baseURI = "http://localhost:7000/api";
    }

    @BeforeEach
    void beforeEach()
    {
        try (EntityManager em = emf.createEntityManager())
        {
            hotelTest1 = new Hotel("First test Hotel", "avenue silver streete");
            hotelTest2 = new Hotel("Second test hotel", "Bronze age avenue");

            em.getTransaction().begin();
            em.createQuery("DELETE FROM Room").executeUpdate();
            em.createQuery("DELETE FROM Hotel").executeUpdate();

            em.persist(hotelTest1);
            em.persist(hotelTest2);
            em.flush(); // force persistnce immediately
            em.getTransaction().commit();
            em.clear(); // detach entities from persistence context
        } catch (Exception e)
        {
            logger.error("error setting up test", e);
            fail();
        }
    }

    @Test
    void getAll()
    {
        given()
            .when()
            .get("/hotel")
            .then()
            .statusCode(200)
            .body("size()", equalTo(2));
    }

    @Test
    void getById()
    {
        given().when()
            .pathParam("id", hotelTest2.getId())
            .contentType("application/json")
            .accept("application/json")
            .get("/hotel/{id}")
            .then()
            .statusCode(200)
            .body("id", equalTo( hotelTest2.getId().intValue()));
    }

    @Test
    void create()
    {
        Hotel entity = new Hotel("Hilton Hotel", "Golden Avenue");
        Room room = new Room(200);
        entity.addRoom(room);
        try
        {
            String json = objectMapper.writeValueAsString(new HotelDTO(entity));
            given().when()
                .contentType("application/json")
                .accept("application/json")
                .body(json)
                .post("/hotel")
                .then()
                .statusCode(200)
                .body("name", equalTo(entity.getName()));
        } catch (Exception e)
        {
            logger.error("Error creating test hotel");
            fail();
        }
    }

    @Test
    void update()
    {
        Hotel entity = new Hotel("Updated hotel", "new location");
        try
        {
            String json = objectMapper.writeValueAsString(new HotelDTO(entity));
            given().when()
                .pathParam("id", hotelTest1.getId())
                .accept("application/json")
                .contentType("application/json")
                .body(json)
                .put("/hotel/{id}")
                .then()
                .statusCode(200)
                .body("name", equalTo(entity.getName()));
        } catch (JsonProcessingException e)
        {
            logger.error("Error updating entity", e);
            fail();
        }
    }

    @Test
    void delete()
    {
        given().when()
            .pathParam("id", hotelTest1.getId())
            .delete("/hotel/{id}")
            .then()
            .statusCode(200);
    }

}
