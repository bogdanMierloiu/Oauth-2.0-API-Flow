package ro.bogdanmierloiu.Oauth2APIFlow.mapper;

import ro.bogdanmierloiu.Oauth2APIFlow.dto.RoleResponse;
import ro.bogdanmierloiu.Oauth2APIFlow.dto.UserResponse;
import ro.bogdanmierloiu.Oauth2APIFlow.entity.Role;
import ro.bogdanmierloiu.Oauth2APIFlow.entity.User;

import java.util.List;

public class UserMapper {

    private UserMapper() {
    }

    public static UserResponse entityToDto(User user) {
        return UserResponse.builder()
                .email(user.getEmail())
                .name(user.getName())
                .surname(user.getSurname())
                .roles(RoleMapper.entityToDto(user.getRoles()))
                .uuid(user.getUuid())
                .build();
    }

    public static List<UserResponse> entityToDto(List<User> roles) {
        return roles.stream()
                .map(UserMapper::entityToDto)
                .toList();
    }
}
