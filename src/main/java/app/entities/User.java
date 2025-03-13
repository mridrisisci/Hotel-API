package app.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;

import java.util.HashSet;
import java.util.Set;

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
    public boolean verifyPassword(String pw)
    {
        return BCrypt.checkpw(pw, this.password);
    }

    @Override
    public void addRole(Role role)
    {
        roles.add(role);
        role.users.add(this);
    }

    @Override
    public void removeRole(String role)
    {
        roles.removeIf(r -> r.getName().equals(role));
        roles.stream()
            .filter(r -> r.getName().equals(role))
            .findFirst()
            .ifPresent(r -> r.getUsers().remove(this));
    }
}
