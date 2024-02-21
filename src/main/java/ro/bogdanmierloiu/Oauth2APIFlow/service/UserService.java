package ro.bogdanmierloiu.Oauth2APIFlow.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.bogdanmierloiu.Oauth2APIFlow.dto.UserResponse;
import ro.bogdanmierloiu.Oauth2APIFlow.entity.User;
import ro.bogdanmierloiu.Oauth2APIFlow.exception.NotFoundException;
import ro.bogdanmierloiu.Oauth2APIFlow.mapper.UserMapper;
import ro.bogdanmierloiu.Oauth2APIFlow.repo.RoleRepo;
import ro.bogdanmierloiu.Oauth2APIFlow.repo.UserRepo;
import ro.bogdanmierloiu.Oauth2APIFlow.util.ErrorMessages;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepo;
    private final RoleService roleService;
    private static final String USER = "User";

    @Transactional
    public UserResponse save(User user) {
        return UserMapper.entityToDto(userRepo.save(user));
    }

    @Transactional(readOnly = true)
    public UserResponse getByUuid(UUID uuid) {
        return UserMapper.entityToDto(userRepo.findByUuid(uuid).orElseThrow(
                () -> new NotFoundException(ErrorMessages.objectWithUuidNotFound(USER, uuid))));
    }

    @Transactional(readOnly = true)
    public UserResponse getByEmail(String email) {
        return UserMapper.entityToDto(userRepo.findByEmail(email).orElseThrow(
                () -> new NotFoundException(USER + " with email " + email + " not found.")));
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAll() {
        return UserMapper.entityToDto(userRepo.findAll());
    }


    /**
     * Verifies if the user exists in the database and saves it if it doesn't.
     *
     * @param jwt The JWT object containing the user data.
     * @return The user object representing the verified user.
     */
    @Transactional
    public User verifyExistAndSave(Jwt jwt) {
        String email = jwt.getClaimAsString("email");
        Optional<User> appUser = userRepo.findByEmail(email);
        if (appUser.isPresent()) {
            return appUser.get();
        }
        User userToSave = User.builder()
                .name(jwt.getClaimAsString("name"))
                .surname(jwt.getClaimAsString("surname"))
                .email(email)
                .roles(new HashSet<>())
                .products(new HashSet<>())
                .build();
        userToSave.getRoles().add(roleService.getMemberRole());
        return userRepo.save(userToSave);
    }


}
