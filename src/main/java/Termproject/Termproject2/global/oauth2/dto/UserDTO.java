package Termproject.Termproject2.global.oauth2.dto;

import Termproject.Termproject2.domain.user.entity.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
    private Long userId;
    private Role role;
    private String username;
    private String name;
    private boolean isNewUser;
}
