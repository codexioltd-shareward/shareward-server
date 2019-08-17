package bg.codexio.shareward.repository;

import bg.codexio.shareward.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Set<Account> findAllByNameStartingWith(String name);

}
