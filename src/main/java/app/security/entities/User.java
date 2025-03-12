package app.security.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.mindrot.jbcrypt.BCrypt;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Table(name = "accounts")
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

    public User() {}

    @Override
    public Set<String> getRolesAsStrings()
    {
        return Set.of();
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
        roles.remove(role);
        for (Role ent : roles)
        {
            roles.remove(ent);
            ent.users.remove(this);
        }
    }
}
