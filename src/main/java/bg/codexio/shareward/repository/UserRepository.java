package bg.codexio.shareward.repository;

import bg.codexio.shareward.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findFirstByEmail(String email);

    Set<User> findAllByEmailStartingWith(String email);
}
