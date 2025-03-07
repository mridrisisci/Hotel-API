package app.rest;

import app.config.HibernateConfig;
import app.controllers.HotelController;
import io.javalin.apibuilder.EndpointGroup;
import jakarta.persistence.EntityManagerFactory;

import static io.javalin.apibuilder.ApiBuilder.*;

public class Routes
{
    private static EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
    private static HotelController hotelController = new HotelController(emf);

    public static EndpointGroup getRoutes()
    {
        return () ->
        {
            path("/hotel", () ->
            {
                get("/", hotelController::getAll);
                get("/{id}", hotelController::getById);
                get("/{id}/rooms", hotelController::getRooms);
                post("/", hotelController::create);
                put("/{id}", hotelController::update);
                delete("/{id}", hotelController::delete);
            });
        };
    }
}
