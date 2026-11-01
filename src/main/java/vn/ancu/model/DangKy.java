package vn.ancu.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.*;

/** Entity ánh xạ bảng DangKy trong PostgreSQL. */
@Entity
@Table(name="DangKy")
public class DangKy {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="customerId")
    private Long customerId;

    @Column(name="houseId")
    private Long houseId;

    @Column(name="date")
    private LocalDate date;

    @Column(name="status", length=30)
    private String status;

    @Column(name="note", length=1000)
    private String note;

    public DangKy() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public Long getHouseId() { return houseId; }
    public void setHouseId(Long houseId) { this.houseId = houseId; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

}
