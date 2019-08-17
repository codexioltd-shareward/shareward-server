package bg.codexio.shareward.repository;

import bg.codexio.shareward.entity.Account;
import bg.codexio.shareward.entity.PaymentRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Set;

public interface PaymentRequestRepository extends JpaRepository<PaymentRequest, Long> {

    Set<PaymentRequest> findAllBySenderIn(Collection<Account> sender);

}
