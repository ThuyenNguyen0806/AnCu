package vn.ancu.repository;

import vn.ancu.model.NguoiDung;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.*;

public interface NguoiDungRepository extends JpaRepository<NguoiDung, Long> {
    boolean existsByCccd(String cccd);
}
