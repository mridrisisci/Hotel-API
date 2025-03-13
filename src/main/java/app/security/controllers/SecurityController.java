package app.security.controllers;

//mport app.dtos.UserDTO;
import app.dtos.UserDTO;
import app.entities.User;
import app.security.daos.UserDAO;
import io.javalin.http.Handler;

import java.util.Set;

public class SecurityController implements ISecurityController
{

    private UserDAO userDAO = new UserDAO();

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
            userDAO.create(newUser);
            ctx.json(newUser);
        };
    }

    @Override
    public Handler authenticate()
    {
        return null;
    }

    @Override
    public boolean authorize(UserDTO userDTO, Set<String> allowedRoles)
    {
        return false;
    }

    @Override
    public String createToken(UserDTO user) throws Exception
    {
        return "";
    }

    @Override
    public UserDTO verifyToken(String token) throws Exception
    {
        return null;
    }
}
