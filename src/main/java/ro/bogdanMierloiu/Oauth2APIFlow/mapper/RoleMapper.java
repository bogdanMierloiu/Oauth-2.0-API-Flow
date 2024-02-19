package ro.bogdanMierloiu.Oauth2APIFlow.mapper;

import ro.bogdanMierloiu.Oauth2APIFlow.dto.RoleResponse;
import ro.bogdanMierloiu.Oauth2APIFlow.entity.Role;

import java.util.List;

public class RoleMapper {

    private RoleMapper() {
    }

    public static RoleResponse entityToDto(Role role) {
        return RoleResponse.builder()
                .name(role.getName())
                .build();
    }

    public static List<RoleResponse> entityToDto(List<Role> roles) {
        return roles.stream()
                .map(RoleMapper::entityToDto)
                .toList();
    }
}
