package vn.ancu.controller;

import jakarta.validation.Valid;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.ancu.model.Models.*;
import vn.ancu.service.HousingService;

/** MVC giống BankController: GET lấy Model; POST gọi Service rồi redirect. */
@Controller
public class PageController {
    private final HousingService service;
    public PageController(HousingService service) { this.service = service; }

    @GetMapping({"/", "/app"})
    public String home(Model model) {
        model.addAttribute("houses", service.publicHouses());
        model.addAttribute("requestForm", new VisitorRequest(null, "", "", "", "", "PURCHASE", null, null, ""));
        return "public-home";
    }

    @GetMapping("/login")
    public String login() { return "login"; }

    @GetMapping("/housing/dashboard")
    public String dashboard(Model model,
            @RequestParam(defaultValue="houses") String tab) {
        model.addAttribute("data", service.data());
        model.addAttribute("activeTab", tab);
        model.addAttribute("systemNotice", "Dữ liệu được lưu trong PostgreSQL.");
        return "housing-dashboard";
    }

    @PostMapping("/housing/houses")
    public String house(@Valid @ModelAttribute House form, BindingResult errors, RedirectAttributes flash) {
        return save("houses", errors, flash, () -> service.house(form));
    }

    @PostMapping("/housing/customers")
    public String customer(@Valid @ModelAttribute Customer form, BindingResult errors, RedirectAttributes flash) {
        return save("customers", errors, flash, () -> service.customer(form));
    }

    @PostMapping("/housing/registrations")
    public String registration(@Valid @ModelAttribute Registration form, BindingResult errors, RedirectAttributes flash) {
        return save("registrations", errors, flash, () -> service.registration(form));
    }

    @PostMapping("/housing/appointments")
    public String appointment(@Valid @ModelAttribute Appointment form, BindingResult errors, RedirectAttributes flash) {
        return save("appointments", errors, flash, () -> service.appointment(form));
    }

    private String save(String tab, BindingResult errors, RedirectAttributes flash, Runnable action) {
        if (errors.hasErrors()) {
            flash.addFlashAttribute("error", "Thông tin chưa hợp lệ. Kiểm tra trường bắt buộc, số và định dạng.");
        } else {
            try {
                action.run();
                flash.addFlashAttribute("message", "Đã lưu thông tin thành công.");
            } catch (IllegalArgumentException e) {
                flash.addFlashAttribute("error", e.getMessage());
            } catch (DataAccessException e) {
                flash.addFlashAttribute("error", "Không thể lưu dữ liệu: kiểm tra mã trùng, dữ liệu liên quan hoặc kết nối PostgreSQL.");
            }
        }
        return "redirect:/housing/dashboard?tab=" + tab;
    }
}
