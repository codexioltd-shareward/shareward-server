package bg.codexio.shareward.repository;

import bg.codexio.shareward.entity.Fund;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FundRepository extends JpaRepository<Fund, Long> {
}
