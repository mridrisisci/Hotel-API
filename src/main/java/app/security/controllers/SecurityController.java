package app.security.controllers;

import app.config.HibernateConfig;
import app.daos.UserDAO;
import com.fasterxml.jackson.databind.ObjectMapper;
import dk.bugelhartmann.ITokenSecurity;
import dk.bugelhartmann.TokenSecurity;
import io.javalin.http.Handler;
import jakarta.persistence.EntityManagerFactory;

public class SecurityController implements ISecurityController
{

    ObjectMapper objectMapper = new ObjectMapper();
    ITokenSecurity iTokenSecurity = new TokenSecurity();
    private static final EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
    private UserDAO userDAO = UserDAO.getInstance(emf);

    @Override
    public Handler authorize()
    {
        return (ctx) ->
        {

        };
    }

    @Override
    public Handler authenticate()
    {
        return (ctx) ->
        {

        };
    }

    @Override
    public Handler login()
    {
        return null;
    }

    @Override
    public Handler register()
    {
        return (ctx) ->
        {
            dk.bugelhartmann.UserDTO newUser = ctx.bodyAsClass(dk.bugelhartmann.UserDTO.class);
            userDAO.create(new User(newUser.getUsername(), newUser.getPassword()));
            ctx.json(newUser);
        };
    }

}
