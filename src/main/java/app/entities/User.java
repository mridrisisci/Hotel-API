package app.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "accounts")
@NamedQueries(@NamedQuery(name = "User.deleteAllRows", query = "DELETE from User"))
public class User implements ISecurityUser
{

    @Id
    private String username;

    private String password;

    @ManyToMany
    @JoinTable(
        name = "user_role",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    Set<Role> roles = new HashSet<>();


    public User(String username, String password)
    {
        this.username = username;
        this.password = BCrypt.hashpw(password, BCrypt.gensalt());
    }
    @Override
    public Set<String> getRolesAsStrings()
    {

        return roles.stream()
            .map(Role::getRoleName).collect(Collectors.toSet());
    }

    @Override
    public boolean verifyPassword(String pw)
    {
        return BCrypt.checkpw(pw, this.password);
    }



    @Override
    public User addRole(Role role)
    {
        roles.add(role);
        role.users.add(this); // "admin".users.add(Morten)
        return this;
    }

    @Override
    public void removeRole(String role)
    {
        roles.removeIf(r -> r.getRoleName().equals(role));
        roles.stream()
            .filter(r -> r.getRoleName().equals(role))
            .findFirst()
            .ifPresent(r -> r.getUsers().remove(this));
    }


}
