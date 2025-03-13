package app.rest;

import app.config.HibernateConfig;
import app.controllers.HotelController;
import app.dtos.UserDTO;
import app.enums.Role;
import app.security.controllers.ISecurityController;
import app.security.controllers.SecurityController;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.http.Handler;
import jakarta.persistence.EntityManagerFactory;

import java.util.Set;

import static io.javalin.apibuilder.ApiBuilder.*;

public class Routes
{
    private static EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
    private static HotelController hotelController = new HotelController(emf);
    private static ISecurityController iSecurityController = new SecurityController();
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
                post("/register", ISecurityController.register());
                post("/login", ISecurityController.login());
            });
            path("/secured", () ->
            {
                get("demo", (ctx) -> ctx.json(objectMapper.createObjectNode().put("msg","success")), Role.ACCOUNT);
                before(ctx-> iSecurityController.authenticate());
                before(ctx-> iSecurityController.authorize());

            });
        };
    }
}
