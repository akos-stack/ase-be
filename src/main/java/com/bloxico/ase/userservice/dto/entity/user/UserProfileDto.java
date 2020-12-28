package com.bloxico.ase.userservice.dto.entity.user;

import com.bloxico.ase.userservice.dto.entity.address.LocationDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Stream;

@Data
@EqualsAndHashCode(of = "email")
@ToString(exclude = "password")
public class UserProfileDto {

    private Long id;
    private String name;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private LocalDate birthday;
    private String gender;
    private LocationDto location;
    private Boolean locked = false;
    private Boolean enabled = false;
    private String provider;
    private String providerId;
    private Set<RoleDto> roles;

    public Stream<String> streamRoleNames() {
        return roles
                .stream()
                .map(RoleDto::getName);
    }

}
