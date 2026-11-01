package vn.ancu.repository;

import vn.ancu.model.DangKy;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.*;

public interface DangKyRepository extends JpaRepository<DangKy, Long> {
    List<DangKy> findByHouseId(Long houseId);
    boolean existsByHouseIdAndStatus(Long houseId, String status);
}
