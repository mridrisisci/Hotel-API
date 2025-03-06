package app.rest;

import app.config.HibernateConfig;
import io.javalin.apibuilder.EndpointGroup;
import jakarta.persistence.EntityManagerFactory;

import static io.javalin.apibuilder.ApiBuilder.path;

public class Routes
{
    private static EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();

    public static EndpointGroup getRoutes()
    {
        return () ->
        {
            path("/", () ->
            {
               // routes go here
            });
        };
    }
}
