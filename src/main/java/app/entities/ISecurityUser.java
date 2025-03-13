package app.entities;

public interface ISecurityUser
{
    //Set<String> getRolesAsStrings();
    boolean verifyPassword(String pw);
    void addRole(Role role);
    void removeRole(String role);
}
