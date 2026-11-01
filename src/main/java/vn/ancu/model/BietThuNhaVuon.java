package vn.ancu.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.*;

/** Entity ánh xạ bảng BietThuNhaVuon trong PostgreSQL. */
@Entity
@Table(name="BietThuNhaVuon")
public class BietThuNhaVuon {
    @Id
    @Column(name="houseId")
    private Long houseId;

    @Column(name="garden", precision=12, scale=2)
    private BigDecimal garden;

    @Column(name="pool", precision=12, scale=2)
    private BigDecimal pool;

    public BietThuNhaVuon() {}

    public Long getHouseId() { return houseId; }
    public void setHouseId(Long houseId) { this.houseId = houseId; }

    public BigDecimal getGarden() { return garden; }
    public void setGarden(BigDecimal garden) { this.garden = garden; }

    public BigDecimal getPool() { return pool; }
    public void setPool(BigDecimal pool) { this.pool = pool; }

}
