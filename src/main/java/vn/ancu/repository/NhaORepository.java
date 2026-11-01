package vn.ancu.repository;

import vn.ancu.model.NhaO;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.*;

public interface NhaORepository extends JpaRepository<NhaO, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select h from NhaO h where h.id = :id")
    Optional<NhaO> findByIdForUpdate(@Param("id") Long id);
    boolean existsByCodeAndIdNot(String code, Long id);
    boolean existsByCode(String code);
}
