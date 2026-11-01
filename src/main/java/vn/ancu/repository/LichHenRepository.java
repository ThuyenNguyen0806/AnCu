package vn.ancu.repository;

import vn.ancu.model.LichHen;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.*;

public interface LichHenRepository extends JpaRepository<LichHen, Long> {
}
