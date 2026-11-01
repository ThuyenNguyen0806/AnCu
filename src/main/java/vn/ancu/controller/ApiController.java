package vn.ancu.controller;
import vn.ancu.service.HousingService;
import vn.ancu.model.Models.*;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.web.csrf.CsrfToken;
import java.util.*;
@RestController @RequestMapping("/api")
public class ApiController {
 private final HousingService service;
 public ApiController(HousingService service){this.service=service;}
 @GetMapping("/csrf") public CsrfToken csrf(CsrfToken token){return token;}
 @GetMapping("/data") public Map<String,Object> data(){return service.data();}
 @PostMapping("/houses") public Map<String,Boolean> house(@Valid @RequestBody House h){service.house(h);return Map.of("ok",true);}
 @PostMapping("/customers") public Map<String,Boolean> customer(@Valid @RequestBody Customer c){service.customer(c);return Map.of("ok",true);}
 @PostMapping("/registrations") public Map<String,Boolean> registration(@Valid @RequestBody Registration r){service.registration(r);return Map.of("ok",true);}
 @PostMapping("/appointments") public Map<String,Boolean> appointment(@Valid @RequestBody Appointment a){service.appointment(a);return Map.of("ok",true);}
}
