package app.entities;

import java.util.Set;

public interface ISecurityUser
{
    Set<String> getRolesAsStrings();
    boolean verifyPassword(String pw);
    User addRole(Role role);
    User removeRole(String role);
}
