package vn.ancu.service;

import vn.ancu.model.*;
import vn.ancu.model.Models.*;
import vn.ancu.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;

/** Nghiệp vụ tương đương BankService: repository -> kiểm tra -> lưu trong transaction. */
@Service
public class HousingService {
    private final NhaORepository houses;
    private final NguoiDungRepository customers;
    private final BietThuNhaVuonRepository villas;
    private final KhuNghiDuongRepository resorts;
    private final DangKyRepository registrations;
    private final LichHenRepository appointments;

    public HousingService(NhaORepository houses, NguoiDungRepository customers,
            BietThuNhaVuonRepository villas, KhuNghiDuongRepository resorts,
            DangKyRepository registrations, LichHenRepository appointments) {
        this.houses = houses;
        this.customers = customers;
        this.villas = villas;
        this.resorts = resorts;
        this.registrations = registrations;
        this.appointments = appointments;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> data() {
        Map<Long, BietThuNhaVuon> gardenByHouse = new HashMap<>();
        villas.findAll().forEach(v -> gardenByHouse.put(v.getHouseId(), v));
        Map<Long, KhuNghiDuong> resortByHouse = new HashMap<>();
        resorts.findAll().forEach(r -> resortByHouse.put(r.getHouseId(), r));
        List<House> list = houses.findAll().stream().map(h -> {
            var v = gardenByHouse.get(h.getId());
            var r = resortByHouse.get(h.getId());
            return new House(h.getId(), h.getCode(), h.getName(), h.getType(),
                    h.getAddress(), h.getArea(), h.getPrice(), h.getStatus(), h.getRooms(),
                    v == null ? BigDecimal.ZERO : v.getGarden(),
                    v == null ? BigDecimal.ZERO : v.getPool(),
                    r == null ? 0 : r.getAmenities(), h.getImage(), h.getDescription());
        }).toList();
        return Map.of("houses", list, "customers", customers.findAll(),
                "registrations", registrations.findAll(), "appointments", appointments.findAll());
    }

    private NhaO lockHouse(Long id) {
        return houses.findByIdForUpdate(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhà ở."));
    }

    private void requireCustomer(Long id) {
        if (!customers.existsById(id)) throw new IllegalArgumentException("Khách hàng không tồn tại.");
    }

    @Transactional
    public void house(House input) {
        NhaO h = input.id() == null ? new NhaO() : lockHouse(input.id());
        String code = input.code().trim();
        boolean duplicate = input.id() == null ? houses.existsByCode(code)
                : houses.existsByCodeAndIdNot(code, input.id());
        if (duplicate) throw new IllegalArgumentException("Mã nhà ở đã tồn tại.");
        if (input.id() != null && registrations.existsByHouseIdAndStatus(input.id(), "Đã duyệt")
                && input.status().equals("Sẵn bán")) {
            throw new IllegalArgumentException("Nhà có hồ sơ đã duyệt, không thể chuyển lại sẵn bán.");
        }
        h.setCode(code);
        h.setName(input.name().trim());
        h.setType(input.type());
        h.setAddress(input.address().trim());
        h.setArea(input.area());
        h.setPrice(input.price());
        h.setStatus(input.status());
        h.setRooms(input.rooms());
        h.setImage(input.image());
        h.setDescription(input.description());
        houses.saveAndFlush(h);
        if (input.type().equals("VILLA")) {
            resorts.findById(h.getId()).ifPresent(resorts::delete);
            var villa = villas.findById(h.getId()).orElseGet(BietThuNhaVuon::new);
            villa.setHouseId(h.getId());
            villa.setGarden(input.garden());
            villa.setPool(input.pool());
            villas.save(villa);
        } else {
            villas.findById(h.getId()).ifPresent(villas::delete);
            var resort = resorts.findById(h.getId()).orElseGet(KhuNghiDuong::new);
            resort.setHouseId(h.getId());
            resort.setAmenities(input.amenities());
            resorts.save(resort);
        }
    }

    @Transactional
    public void customer(Customer input) {
        if (input.id() != null) throw new IllegalArgumentException("Chức năng này chỉ thêm khách hàng mới.");
        if (customers.existsByCccd(input.cccd())) throw new IllegalArgumentException("CCCD đã được đăng ký.");
        var customer = new NguoiDung();
        customer.setName(input.name().trim());
        customer.setCccd(input.cccd());
        customer.setPhone(input.phone());
        customer.setEmail(input.email().trim());
        customers.save(customer);
    }

    @Transactional
    public void registration(Registration input) {
        var house = lockHouse(input.houseId());
        requireCustomer(input.customerId());
        if (input.id() == null) {
            if (!input.status().equals("Chờ duyệt") || input.date().isAfter(LocalDate.now()))
                throw new IllegalArgumentException("Hồ sơ mới phải chờ duyệt và không được ghi ngày tương lai.");
            if (!house.getStatus().equals("Sẵn bán")) throw new IllegalArgumentException("Nhà không còn sẵn bán.");
            boolean duplicate = registrations.findByHouseId(input.houseId()).stream()
                    .anyMatch(r -> r.getCustomerId().equals(input.customerId()) && !r.getStatus().equals("Đã hủy"));
            if (duplicate) throw new IllegalArgumentException("Khách đã đăng ký mua nhà này.");
            var registration = new DangKy();
            registration.setCustomerId(input.customerId());
            registration.setHouseId(input.houseId());
            registration.setDate(input.date());
            registration.setStatus("Chờ duyệt");
            registration.setNote(input.note());
            registrations.save(registration);
            return;
        }
        var old = registrations.findById(input.id())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đăng ký."));
        if (!old.getHouseId().equals(input.houseId()) || !old.getCustomerId().equals(input.customerId())
                || !old.getStatus().equals("Chờ duyệt") || !Set.of("Đã duyệt", "Đã hủy").contains(input.status()))
            throw new IllegalArgumentException("Chuyển trạng thái không hợp lệ.");
        if (input.status().equals("Đã duyệt")) {
            if (!house.getStatus().equals("Sẵn bán")) throw new IllegalArgumentException("Nhà đã có người đặt chỗ hoặc đã bán.");
            house.setStatus("Đã đặt chỗ");
            houses.save(house);
        }
        old.setStatus(input.status());
        registrations.save(old);
    }

    @Transactional
    public void appointment(Appointment input) {
        var house = lockHouse(input.houseId());
        requireCustomer(input.customerId());
        if (input.id() == null) {
            if (house.getStatus().equals("Đã bán")) throw new IllegalArgumentException("Nhà đã bán.");
            if (!input.status().equals("Chờ xác nhận") || LocalDateTime.of(input.date(), input.time()).isBefore(LocalDateTime.now()))
                throw new IllegalArgumentException("Ngày giờ hẹn phải ở tương lai.");
            boolean duplicate = appointments.findAll().stream().anyMatch(a -> a.getHouseId().equals(input.houseId())
                    && a.getDate().equals(input.date()) && a.getTime().equals(input.time()) && !a.getStatus().equals("Đã hủy"));
            if (duplicate) throw new IllegalArgumentException("Nhà đã có lịch hẹn vào thời điểm này.");
            var appointment = new LichHen();
            appointment.setCustomerId(input.customerId());
            appointment.setHouseId(input.houseId());
            appointment.setDate(input.date());
            appointment.setTime(input.time());
            appointment.setStatus("Chờ xác nhận");
            appointment.setNote(input.note());
            appointments.save(appointment);
            return;
        }
        var old = appointments.findById(input.id())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy lịch hẹn."));
        boolean valid = old.getStatus().equals("Chờ xác nhận") && Set.of("Đã xác nhận", "Đã hủy").contains(input.status())
                || old.getStatus().equals("Đã xác nhận") && Set.of("Hoàn thành", "Đã hủy").contains(input.status());
        if (!valid || !old.getHouseId().equals(input.houseId()) || !old.getCustomerId().equals(input.customerId()))
            throw new IllegalArgumentException("Chuyển trạng thái không hợp lệ.");
        old.setStatus(input.status());
        appointments.save(old);
    }
}
