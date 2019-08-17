package bg.codexio.shareward.repository;

import bg.codexio.shareward.entity.Account;
import bg.codexio.shareward.entity.Payment;
import bg.codexio.shareward.entity.PaymentRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Set<Payment> findAllBySender(Account account);

    Set<Payment> findAllByReceiver(Account account);
}
