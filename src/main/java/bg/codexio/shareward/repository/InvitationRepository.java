package bg.codexio.shareward.repository;

import bg.codexio.shareward.entity.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    Set<Invitation> findByAccountId(Long accountId);
}
