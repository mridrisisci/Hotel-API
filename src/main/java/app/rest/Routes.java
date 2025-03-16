package app.rest;

import app.config.HibernateConfig;
import app.controllers.HotelController;
import app.enums.Role;
import app.security.controllers.ISecurityController;
import app.security.controllers.SecurityController;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.apibuilder.EndpointGroup;
import jakarta.persistence.EntityManagerFactory;

import static io.javalin.apibuilder.ApiBuilder.*;

public class Routes
{
    private static EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
    private static HotelController hotelController = new HotelController(emf);
    private static ISecurityController securityController = new SecurityController();
    private static ObjectMapper objectMapper = new ObjectMapper();

    public static EndpointGroup getRoutes()
    {
        return () ->
        {
            path("/hotel", () ->
            {
                get("/{id}", hotelController::getById);
                get("/", hotelController::getHotels);
                post("/", hotelController::create);
                put("/{id}", hotelController::update);
                delete("/{id}", hotelController::delete);
            });
            path("/room", () ->
            {
                // add rooms to hotel ?
            });
            path("hotel/{id}/rooms", () ->
            {
                get(hotelController::getRooms);
            });
            path("/auth", () ->
            {
                post("/register", securityController.register());
                post("/login", securityController.login());
            });
            path("/secured", () ->
            {
                get("demo", ctx -> ctx.json(objectMapper.createObjectNode().put("demo","its friday bitch")), Role.USER);

            });
        };
    }
}
