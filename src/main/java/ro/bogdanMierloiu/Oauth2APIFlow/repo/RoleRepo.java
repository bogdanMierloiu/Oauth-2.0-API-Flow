package ro.bogdanMierloiu.Oauth2APIFlow.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ro.bogdanMierloiu.Oauth2APIFlow.entity.Role;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepo extends JpaRepository<Role, Long> {

    Optional<Role> findByUuid(UUID uuid);
}
