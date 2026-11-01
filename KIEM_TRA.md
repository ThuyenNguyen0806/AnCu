# Kiểm tra bản PostgreSQL — 01/11/2026

- Maven verify bằng JDK 21: thành công, đóng gói Spring Boot JAR thành công.
- 10 kiểm thử Spring MVC / service: đạt, không lỗi, không bỏ qua. Gồm 8 kiểm thử nghiệp vụ / MVC cũ và 2 kiểm thử đăng nhập thật bằng đúng/sai mật khẩu.
- Các kiểm thử Java dùng H2 MODE PostgreSQL để kiểm tra hành vi ứng dụng; không thay thế tích hợp JDBC với PostgreSQL trên Render.
- Script schema và dữ liệu mẫu đã chạy trên engine PostgreSQL qua PGlite: tạo đủ 6 nhà, 4 chi tiết biệt thự, 2 chi tiết nghỉ dưỡng; chạy lại không nhân bản hoặc ghi đè trạng thái; không phục hồi nhầm chi tiết loại cũ; unique index chặn hai đăng ký đã duyệt cho cùng một nhà.
- render.yaml hợp lệ theo JSON Schema chính thức tải từ https://render.com/schema/render.yaml.json tại thời điểm kiểm tra. Chưa chạy Render CLI hoặc kiểm tra ràng buộc tài khoản/workspace trên server.
- app.js: kiểm tra cú pháp thành công.
- Giữ 6 ảnh JPEG có sẵn trong bản nguồn.
- Chưa chạy Docker build, chưa triển khai Render, chưa kiểm tra kết nối JDBC đến database Render thật. Cần quan sát log và thử chức năng sau khi deploy.

Schema dùng CREATE TABLE IF NOT EXISTS để khởi tạo database mới; không phải bộ migration cho database cũ có cấu trúc khác. Mỗi bản online này nên dùng database riêng, không ghép với bảng của bài khác.
