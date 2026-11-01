package vn.ancu.controller;

import jakarta.validation.Valid;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.ancu.model.Models.VisitorRequest;
import vn.ancu.service.HousingService;

@Controller
public class PublicController {
    private final HousingService service;
    public PublicController(HousingService service) { this.service = service; }

    @PostMapping("/public/requests")
    public String request(@Valid @ModelAttribute("requestForm") VisitorRequest form,
            BindingResult errors, RedirectAttributes flash) {
        if (errors.hasErrors()) {
            flash.addFlashAttribute("error", "Vui lòng kiểm tra thông tin: họ tên, CCCD 12 số, điện thoại và email hợp lệ.");
        } else {
            try {
                service.visitorRequest(form);
                flash.addFlashAttribute("message", "Đã gửi yêu cầu. Quản trị viên sẽ liên hệ với bạn để xác nhận.");
            } catch (IllegalArgumentException ex) {
                flash.addFlashAttribute("error", ex.getMessage());
            } catch (DataAccessException ex) {
                flash.addFlashAttribute("error", "Chưa thể gửi yêu cầu. Vui lòng thử lại sau.");
            }
        }
        return "redirect:/#request";
    }
}
