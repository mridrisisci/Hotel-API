package app;

import app.config.HibernateConfig;
import app.rest.ApplicationConfig;
import app.rest.Routes;
import io.javalin.Javalin;
import jakarta.persistence.EntityManagerFactory;
import app.controllers.HotelController;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;

public class Main
{
    final static EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
    final static HotelController hotelController = new HotelController(emf);

    public static void main(String[] args)
    {
        hotelController.populateDB();
        ApplicationConfig
            .getInstance()
            .initiateServer()
            .setRoute(Routes.getRoutes())
            .handleExceptions()
            .startServer(7000);

    }
}