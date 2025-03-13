package app.enums;

import io.javalin.security.RouteRole;

public enum Role implements RouteRole
{
    ANYONE,
    ACCOUNT,
    ADMIN
}
