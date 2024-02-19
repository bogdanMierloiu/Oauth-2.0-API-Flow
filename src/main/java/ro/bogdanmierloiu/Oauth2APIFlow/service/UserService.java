package ro.bogdanmierloiu.Oauth2APIFlow.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ro.bogdanmierloiu.Oauth2APIFlow.dto.UserResponse;
import ro.bogdanmierloiu.Oauth2APIFlow.entity.User;
import ro.bogdanmierloiu.Oauth2APIFlow.exception.NotFoundException;
import ro.bogdanmierloiu.Oauth2APIFlow.mapper.UserMapper;
import ro.bogdanmierloiu.Oauth2APIFlow.repo.UserRepo;
import ro.bogdanmierloiu.Oauth2APIFlow.util.ErrorMessages;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepo;
    private static final String USER = "User";

    public UserResponse save(User user) {
        return UserMapper.entityToDto(userRepo.save(user));
    }

    public UserResponse getByUuid(UUID uuid) {
        return UserMapper.entityToDto(userRepo.findByUuid(uuid).orElseThrow(
                () -> new NotFoundException(ErrorMessages.objectWithUuidNotFound(USER, uuid))));
    }

    public UserResponse getByEmail(String email) {
        return UserMapper.entityToDto(userRepo.findByEmail(email).orElseThrow(
                () -> new NotFoundException(USER + " with email " + email + " not found.")));
    }

    public List<UserResponse> getAll() {
        return UserMapper.entityToDto(userRepo.findAll());
    }


}
