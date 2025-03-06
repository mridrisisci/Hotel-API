package app;

import app.rest.ApplicationConfig;
import app.rest.Routes;
import io.javalin.Javalin;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;

public class Main
{
    public static void main(String[] args)
    {
        ApplicationConfig
            .getInstance()
            .initiateServer()
            .setRoute(Routes.getRoutes())
            .handleExceptions()
            .startServer(7000);

    }
}