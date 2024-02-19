package ro.bogdanmierloiu.Oauth2APIFlow.mapper;

import ro.bogdanmierloiu.Oauth2APIFlow.dto.RoleResponse;
import ro.bogdanmierloiu.Oauth2APIFlow.entity.Role;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RoleMapper {

    private RoleMapper() {
    }

    public static RoleResponse entityToDto(Role role) {
        return RoleResponse.builder()
                .name(role.getName())
                .uuid(role.getUuid())
                .build();
    }

    public static Set<RoleResponse> entityToDto(Set<Role> roles) {
        return roles.stream()
                .map(RoleMapper::entityToDto)
                .collect(Collectors.toSet());
    }
}
