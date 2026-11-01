package vn.ancu;

import vn.ancu.model.Models.*;
import vn.ancu.repository.*;
import vn.ancu.service.HousingService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@SpringBootTest @AutoConfigureMockMvc @ActiveProfiles("test") @Transactional
class HousingApplicationTest {
    @Autowired HousingService service;
    @Autowired NhaORepository houses;
    @Autowired NguoiDungRepository customers;
    @Autowired DangKyRepository registrations;
    @Autowired LichHenRepository appointments;
    @Autowired BietThuNhaVuonRepository villas;
    @Autowired KhuNghiDuongRepository resorts;
    @Autowired MockMvc mvc;
    Long houseId, customerId;

    @BeforeEach void seed() {
        service.house(new House(null,"BT-TEST","Nhà kiểm thử","VILLA","Địa chỉ mẫu",new BigDecimal("200"),new BigDecimal("5000000000"),"Sẵn bán",3,new BigDecimal("50"),new BigDecimal("20"),0,0,"Mô tả"));
        service.customer(new Customer(null,"Khách mẫu","000000000001","0900000000","demo@example.com"));
        houseId=houses.findAll().getFirst().getId();
        customerId=customers.findAll().getFirst().getId();
    }

    @Test void approvalReservesHouseAndPreventsSecondApproval() {
        service.registration(new Registration(null,customerId,houseId,LocalDate.now(),"Chờ duyệt",""));
        service.customer(new Customer(null,"Khách thứ hai","000000000002","0900000001","other@example.com"));
        Long other=customers.findAll().stream().filter(c->!c.getId().equals(customerId)).findFirst().orElseThrow().getId();
        service.registration(new Registration(null,other,houseId,LocalDate.now(),"Chờ duyệt",""));
        var first=registrations.findAll().stream().filter(r->r.getCustomerId().equals(customerId)).findFirst().orElseThrow();
        service.registration(new Registration(first.getId(),customerId,houseId,first.getDate(),"Đã duyệt",""));
        assertEquals("Đã đặt chỗ",houses.findById(houseId).orElseThrow().getStatus());
        var second=registrations.findAll().stream().filter(r->r.getCustomerId().equals(other)).findFirst().orElseThrow();
        assertThrows(IllegalArgumentException.class,()->service.registration(new Registration(second.getId(),other,houseId,second.getDate(),"Đã duyệt","")));
    }

    @Test void changingHouseTypeMovesSubtypeData() {
        service.house(new House(houseId,"BT-TEST","Nhà nghỉ dưỡng","RESORT","Địa chỉ mẫu",new BigDecimal("200"),new BigDecimal("5000000000"),"Sẵn bán",3,BigDecimal.ZERO,BigDecimal.ZERO,5,0,"Mô tả"));
        houses.flush();
        assertFalse(villas.existsById(houseId));
        assertEquals(5,resorts.findById(houseId).orElseThrow().getAmenities());
    }

    @Test void appointmentRejectsDuplicatesAndAllowsSlotAfterCancel() {
        LocalDate date=LocalDate.now().plusDays(1); LocalTime time=LocalTime.of(10,0);
        service.appointment(new Appointment(null,customerId,houseId,date,time,"Chờ xác nhận","Xem nhà"));
        var a=appointments.findAll().getFirst();
        service.appointment(new Appointment(a.getId(),customerId,houseId,date,time,"Đã hủy","Xem nhà"));
        service.appointment(new Appointment(null,customerId,houseId,date,time,"Chờ xác nhận","Lịch mới"));
        assertEquals(2,appointments.count());
        assertThrows(IllegalArgumentException.class,()->service.appointment(new Appointment(null,customerId,houseId,date,time,"Chờ xác nhận","Trùng lịch")));
    }

    @Test @WithMockUser(roles="ADMIN") void dashboardRendersThymeleafAndSqlModel() throws Exception {
        service.registration(new Registration(null,customerId,houseId,LocalDate.now(),"Chờ duyệt",""));
        service.appointment(new Appointment(null,customerId,houseId,LocalDate.now().plusDays(1),LocalTime.of(10,0),"Chờ xác nhận","Xem nhà"));
        mvc.perform(get("/housing/dashboard")).andExpect(status().isOk())
                .andExpect(view().name("housing-dashboard"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Nhà kiểm thử")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("window.initialData")));
    }

    @Test @WithMockUser(roles="ADMIN") void formPostPersistsAndRedirectsLikeBankingExample() throws Exception {
        long before=customers.count();
        mvc.perform(post("/housing/customers").with(csrf()).param("name","Khách mới")
                .param("cccd","000000000003").param("phone","0900000003").param("email","new@example.com"))
                .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/housing/dashboard?tab=customers"))
                .andExpect(flash().attributeExists("message"));
        assertEquals(before+1,customers.count());
    }

    @Test @WithMockUser(roles="ADMIN") void invalidCustomerDoesNotPersist() throws Exception {
        long before=customers.count();
        mvc.perform(post("/housing/customers").with(csrf()).param("name","Khách lỗi").param("cccd","1")
                .param("phone","abc").param("email","bad"))
                .andExpect(status().is3xxRedirection()).andExpect(flash().attributeExists("error"));
        assertEquals(before,customers.count());
    }

    @Test @WithMockUser(roles="ADMIN") void missingCsrfCannotWrite() throws Exception {
        mvc.perform(post("/housing/customers")).andExpect(status().isForbidden());
    }

    @Test void unauthenticatedVisitorsSeeCustomLogin() throws Exception {
        mvc.perform(get("/housing/dashboard")).andExpect(status().is3xxRedirection());
        mvc.perform(get("/login")).andExpect(status().isOk()).andExpect(view().name("login"));
    }
    @Test void actualFormLoginAcceptsConfiguredPassword() throws Exception {
        mvc.perform(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders
                .formLogin().user("admin").password("TestOnlyPass21"))
                .andExpect(org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated())
                .andExpect(redirectedUrl("/housing/dashboard"));
    }

    @Test void actualFormLoginRejectsWrongPassword() throws Exception {
        mvc.perform(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders
                .formLogin().user("admin").password("WrongPassword"))
                .andExpect(org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated())
                .andExpect(redirectedUrl("/login?error"));
    }
}
