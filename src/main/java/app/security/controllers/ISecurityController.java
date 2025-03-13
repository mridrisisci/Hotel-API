package app.security.controllers;

import app.dtos.UserDTO;
import io.javalin.http.Handler;

import java.util.Set;

public interface ISecurityController
{
    Handler login(); // to get a token after checking username and password
    Handler register(); // to make a new User and get a token
    Handler authenticate(); // to verify that a token was sent with the request and that it is a valid, non-expired token
    boolean authorize(UserDTO userDTO, Set<String> allowedRoles); // to verify user roles
    String createToken(UserDTO user) throws Exception;
    UserDTO verifyToken(String token) throws Exception;

}
