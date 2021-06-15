package dtos.user;

import entities.user.User;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * PublicUserDto provided public information regarding a user.
 * This information could be e.g. used to display a public profile about a user.
 *
 * */

@Data
public class PublicUserDto {
    private String username;
    private String displayName;
    private List<String> roles;

    public PublicUserDto(User user) {
        this.username = user.getUsername();
        this.displayName= user.getDisplayName();
        this.roles = user.getRoles().stream().map(Object::toString).collect(Collectors.toList());
    }
}
