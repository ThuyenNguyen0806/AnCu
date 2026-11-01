package vn.ancu.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.*;

/** Entity ánh xạ bảng NhaO trong PostgreSQL. */
@Entity
@Table(name="NhaO")
public class NhaO {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="code", length=20, unique=true)
    private String code;

    @Column(name="name", length=150)
    private String name;

    @Column(name="type", length=10)
    private String type;

    @Column(name="address", length=255)
    private String address;

    @Column(name="area", precision=12, scale=2)
    private BigDecimal area;

    @Column(name="price", precision=19, scale=0)
    private BigDecimal price;

    @Column(name="status", length=30)
    private String status;

    @Column(name="rooms")
    private int rooms;

    @Column(name="image")
    private int image;

    @Column(name="description", length=2000)
    private String description;

    public NhaO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public BigDecimal getArea() { return area; }
    public void setArea(BigDecimal area) { this.area = area; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getRooms() { return rooms; }
    public void setRooms(int rooms) { this.rooms = rooms; }

    public int getImage() { return image; }
    public void setImage(int image) { this.image = image; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

}
