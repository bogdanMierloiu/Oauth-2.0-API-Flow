package ro.bogdanmierloiu.Oauth2APIFlow.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ro.bogdanmierloiu.Oauth2APIFlow.dto.RoleRequest;
import ro.bogdanmierloiu.Oauth2APIFlow.dto.RoleResponse;
import ro.bogdanmierloiu.Oauth2APIFlow.entity.Role;
import ro.bogdanmierloiu.Oauth2APIFlow.exception.NotFoundException;
import ro.bogdanmierloiu.Oauth2APIFlow.mapper.RoleMapper;
import ro.bogdanmierloiu.Oauth2APIFlow.repo.RoleRepo;
import ro.bogdanmierloiu.Oauth2APIFlow.util.ErrorMessages;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoleService implements CrudService<RoleRequest, RoleResponse> {

    private final RoleRepo roleRepo;
    private static final String ROLE = "Role";

    /**
     * Saves a new role.
     *
     * @param roleRequest The request object containing the role data.
     * @return The response object representing the saved role.
     */
    @Override
    public RoleResponse save(RoleRequest roleRequest) {
        Role roleToSave = Role.builder()
                .name(roleRequest.name())
                .build();
        return RoleMapper.entityToDto(roleRepo.save(roleToSave));
    }

    /**
     * Updates an existing role.
     *
     * @param uuid        The UUID of the role to be updated.
     * @param roleRequest The request object containing the updated role data.
     * @return The response object representing the updated role.
     */
    @Override
    public RoleResponse update(UUID uuid, RoleRequest roleRequest) {
        Role roleToUpdate = roleRepo.findByUuid(uuid).orElseThrow(
                () -> new NotFoundException(ErrorMessages.objectWithUuidNotFound(ROLE, uuid)));
        roleToUpdate.setName(roleRequest.name());
        return RoleMapper.entityToDto(roleRepo.save(roleToUpdate));
    }

    /**
     * Retrieves a role by its UUID.
     *
     * @param uuid The UUID of the role to retrieve.
     * @return The response object representing the retrieved role.
     */
    @Override
    public RoleResponse getByUuid(UUID uuid) {
        return RoleMapper.entityToDto(roleRepo.findByUuid(uuid).orElseThrow(
                () -> new NotFoundException(ErrorMessages.objectWithUuidNotFound(ROLE, uuid))));
    }

    /**
     * Retrieves all roles.
     *
     * @return A list of response objects representing all roles.
     */
    @Override
    public Set<RoleResponse> getAll() {
        return RoleMapper.entityToDto(new HashSet<>(roleRepo.findAll()));
    }

    /**
     * Deletes a role by its UUID.
     *
     * @param uuid The UUID of the role to delete.
     */
    @Override
    public void delete(UUID uuid) {
        Role roleToDelete = roleRepo.findByUuid(uuid).orElseThrow(
                () -> new NotFoundException(ErrorMessages.objectWithUuidNotFound(ROLE, uuid)));
        roleRepo.delete(roleToDelete);
    }

    public Role getMemberRole() {
        return roleRepo.getMemberRole().orElse(
                Role.builder()
                        .name("MEMBER")
                        .build());
    }
}
