package vn.ancu.controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import org.springframework.dao.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import java.util.*;
@RestControllerAdvice
public class ApiErrors {
 @ExceptionHandler(IllegalArgumentException.class) public ResponseEntity<?> invalid(IllegalArgumentException e){return ResponseEntity.badRequest().body(Map.of("message",e.getMessage()));}
 @ExceptionHandler({MethodArgumentNotValidException.class,HttpMessageNotReadableException.class}) public ResponseEntity<?> validation(Exception e){return ResponseEntity.badRequest().body(Map.of("message","Thông tin không hợp lệ. Kiểm tra các trường bắt buộc, định dạng và giá trị."));}
 @ExceptionHandler(DataAccessException.class) public ResponseEntity<?> database(DataAccessException e){return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message","Không lưu được: mã bị trùng, dữ liệu liên quan không tồn tại hoặc cơ sở dữ liệu đang bận."));}
}
