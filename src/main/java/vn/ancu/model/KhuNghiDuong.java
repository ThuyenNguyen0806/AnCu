package vn.ancu.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.*;

/** Entity ánh xạ bảng KhuNghiDuong trong PostgreSQL. */
@Entity
@Table(name="KhuNghiDuong")
public class KhuNghiDuong {
    @Id
    @Column(name="houseId")
    private Long houseId;

    @Column(name="amenities")
    private int amenities;

    public KhuNghiDuong() {}

    public Long getHouseId() { return houseId; }
    public void setHouseId(Long houseId) { this.houseId = houseId; }

    public int getAmenities() { return amenities; }
    public void setAmenities(int amenities) { this.amenities = amenities; }

}
