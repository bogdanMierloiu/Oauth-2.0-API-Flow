package ro.bogdanMierloiu.Oauth2APIFlow.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ro.bogdanMierloiu.Oauth2APIFlow.entity.User;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
}
