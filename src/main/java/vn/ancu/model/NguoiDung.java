package vn.ancu.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.*;

/** Entity ánh xạ bảng NguoiDung trong PostgreSQL. */
@Entity
@Table(name="NguoiDung")
public class NguoiDung {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="name", length=100)
    private String name;

    @Column(name="cccd", length=12, unique=true)
    private String cccd;

    @Column(name="phone", length=15)
    private String phone;

    @Column(name="email", length=150)
    private String email;

    public NguoiDung() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCccd() { return cccd; }
    public void setCccd(String cccd) { this.cccd = cccd; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

}
