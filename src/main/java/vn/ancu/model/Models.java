package vn.ancu.model;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
public final class Models {
 private Models() {}
 public record House(Long id,@NotBlank @Size(max=20) String code,@NotBlank @Size(max=150) String name,@Pattern(regexp="VILLA|RESORT") @NotNull String type,@NotBlank @Size(max=255) String address,@NotNull @DecimalMin("0.01") BigDecimal area,@NotNull @DecimalMin("1") BigDecimal price,@NotNull @Pattern(regexp="Sẵn bán|Đã đặt chỗ|Đã bán") String status,@Min(1) int rooms,@NotNull @DecimalMin("0") BigDecimal garden,@NotNull @DecimalMin("0") BigDecimal pool,@Min(0) int amenities,@Min(0) int image,@Size(max=2000) String description) {}
 public record Customer(Long id,@NotBlank @Size(max=100) String name,@NotNull @Pattern(regexp="[0-9]{12}") String cccd,@NotNull @Pattern(regexp="[0-9+ ]{9,15}") String phone,@NotBlank @Email @Size(max=150) String email) {}
 public record Registration(Long id,@NotNull Long customerId,@NotNull Long houseId,@NotNull @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate date,@NotNull @Pattern(regexp="Chờ duyệt|Đã duyệt|Đã hủy") String status,@Size(max=1000) String note) {}
 public record Appointment(Long id,@NotNull Long customerId,@NotNull Long houseId,@NotNull @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate date,@NotNull @DateTimeFormat(iso=DateTimeFormat.ISO.TIME) LocalTime time,@NotNull @Pattern(regexp="Chờ xác nhận|Đã xác nhận|Hoàn thành|Đã hủy") String status,@NotBlank @Size(max=1000) String note) {}
}
