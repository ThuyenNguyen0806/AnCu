# AN CƯ — BẢN WEB VÀ DATABASE CÙNG TRÊN RENDER

JDK 21 · Spring Boot 3.3.5 · Spring MVC · JPA · Thymeleaf · PostgreSQL.

Đây là bản online riêng, chuyển database từ SQL Server sang PostgreSQL theo lựa chọn của bạn. Giao diện An Cư, sáu ảnh, các chức năng nhà ở / khách hàng / đăng ký / lịch hẹn được giữ. Bản AnCu_SpringBoot_SQLServer.zip trước đó vẫn dùng SQL Server; không cần xóa hoặc sửa database AnCu trên máy.

## Cách triển khai nhanh nhất: Blueprint

### Bước 1 — Tải mã nguồn lên GitHub

1. Giải nén AnCu_Render_PostgreSQL.zip.
2. Tạo một repository mới trên GitHub, ví dụ `ancu-render`.
3. Chọn Add file → Upload files.
4. Mở thư mục AnCu_Render_PostgreSQL trên máy; chọn toàn bộ NỘI DUNG bên trong và kéo vào GitHub.
5. Kiểm tra ở gốc repository phải thấy ngay `render.yaml`, `Dockerfile`, `pom.xml`, thư mục `src` rồi bấm Commit changes.

Không đưa cả thư mục bao ngoài làm một tầng thư mục mới. Không tải thư mục target, file .env hoặc mật khẩu lên GitHub. `.gitignore`/`.dockerignore` đã chuẩn bị sẵn.

### Bước 2 — Tạo web và database cùng lúc trên Render

1. Đăng nhập Render, chọn workspace của bạn.
2. Chọn New + → Blueprint.
3. Kết nối GitHub, chọn repository vừa tạo, chọn nhánh main.
4. Chọn file Blueprint `render.yaml` nếu được hỏi.
5. Render sẽ liệt kê hai tài nguyên:
   - `ancu-web-demo`: Web Service, Docker, Free.
   - `ancu-db-demo`: PostgreSQL 17, Free.
6. Nhập ADMIN_PASSWORD khi Render hỏi. Đây là mật khẩu đăng nhập web do bạn tự đặt. Tên đăng nhập là `admin`.
7. Kiểm tra cả hai tài nguyên đều là Free / $0 trước khi bấm tạo hoặc Apply / Deploy Blueprint. Không chọn gói trả phí.
8. Đợi database Available và web Live. Lần đầu cần tải thư viện và build Java nên có thể mất vài phút.

Nếu workspace đang có database Free và Render không cho tạo thêm, dừng trước khi tạo tài nguyên trùng. Có thể cấu hình web dùng database có sẵn sau khi xác nhận database đó dùng cho bài này; đừng xóa database đang dùng cho bài khác.

### Bước 3 — Mở web

Mở Web Service `ancu-web-demo`, bấm URL do Render cấp. Vào `/login` nếu chưa chuyển tự động.

- Username: `admin`.
- Password: ADMIN_PASSWORD bạn vừa nhập trên Render.

Database tự tạo sáu bảng và sáu nhà mẫu khi ứng dụng khởi động. Không cần chạy file SQL thủ công cho lần deploy mới. Không có khách hàng thật được chuyển từ máy bạn lên; dữ liệu SQL Server cũ không tự đồng bộ sang PostgreSQL.

### Bước 4 — Thử chức năng

Thêm khách hàng mẫu → chọn nhà sẵn bán → tạo đăng ký → duyệt → kiểm tra nhà chuyển sang Đã đặt chỗ. Thử đặt lịch ngày giờ tương lai, xác nhận và hoàn thành hoặc hủy. Tải lại trang để kiểm tra dữ liệu còn được lưu.

## Các biến môi trường

`render.yaml` tự nối database bằng mạng nội bộ; bạn chỉ nhập ADMIN_PASSWORD. Không cần tự sao chép URL database và không dùng tài khoản bank_user của SQL Server.

| Biến | Nguồn |
|---|---|
| DB_HOST | fromDatabase → host |
| DB_PORT | fromDatabase → port |
| DB_NAME | fromDatabase → database |
| DB_USER | fromDatabase → user |
| DB_PASSWORD | fromDatabase → password |
| ADMIN_USER | admin |
| ADMIN_PASSWORD | Bạn nhập trong Render |
| PORT | 10000 |
| SERVER_ADDRESS | 0.0.0.0 |
| TZ | Asia/Ho_Chi_Minh |

Chuỗi JDBC được ghép trong application.properties: `jdbc:postgresql://DB_HOST:DB_PORT/DB_NAME`. Không dán chuỗi `postgresql://user:password@...` vào một ô JDBC. Cấu hình này dùng địa chỉ nội bộ Render; database và web cùng region Singapore, cùng workspace. Database chặn kết nối công khai bằng `ipAllowList: []`.

Nếu đổi mật khẩu web sau này: Web Service → Environment → sửa ADMIN_PASSWORD → lưu và triển khai lại. Không sửa mật khẩu trong source.

## Giới hạn Free cần biết

Theo tài liệu Render đã kiểm tra ngày 01/11/2026:

- PostgreSQL Free hết hạn sau 30 ngày kể từ ngày tạo. Sau hạn này không truy cập được trừ khi nâng cấp; Render có thể xóa sau thời gian gia hạn. Xuất dữ liệu trước khi hết hạn nếu cần giữ bài.
- Web Service Free có thể ngủ sau 15 phút không có truy cập; mở lần tiếp theo có thể phải chờ khởi động.
- Gói Free có hạn mức tài nguyên dùng chung theo workspace. Khi hết hạn mức, dịch vụ có thể tạm dừng.

Tài liệu: https://render.com/docs/free

## Chạy trên máy (tùy chọn)

Cần JDK 21, Maven và PostgreSQL có database rỗng tên `ancu`. Trong IntelliJ nhập DB_HOST, DB_PORT, DB_NAME, DB_USER, DB_PASSWORD, ADMIN_USER, ADMIN_PASSWORD phù hợp với PostgreSQL của bạn. Không dùng thông tin SQL Server.

Chạy AnCuApplication rồi mở http://localhost:8080/housing/dashboard.

Hoặc trong terminal đã khai báo biến môi trường: `mvn clean verify` rồi `java -jar target/quan-ly-nha-o-1.0.0.jar`.

## Cấu trúc và dữ liệu

- Entity → JpaRepository → HousingService → PageController → Thymeleaf, giữ cách tổ chức theo mẫu Banking System.
- `src/main/resources/schema.sql`: tạo bảng/index nếu chưa có.
- `src/main/resources/data.sql`: thêm mẫu theo mã nhà nếu chưa có; không ghi đè giá/trạng thái hiện tại.
- `sql/`: bản script PostgreSQL để tham khảo hoặc quản trị thủ công.
- `src/main/resources/static/images`: ảnh minh họa có sẵn.
- `/health`: kiểm tra truy vấn database, trả 200 nếu kết nối được, 503 nếu lỗi.
- Đăng nhập đã khai báo PasswordEncoder BCrypt dùng chung để tạo và kiểm tra mật khẩu.

Phạm vi là ứng dụng quản trị; khách hàng là hồ sơ liên hệ, chưa có tài khoản tự đăng nhập. Chưa có thanh toán hoặc hợp đồng mua bán.

Xem KIEM_TRA.md để biết kết quả kiểm thử và giới hạn xác minh. Bản ZIP này chưa được đẩy lên GitHub hoặc triển khai thành công trên tài khoản Render của bạn.


## Trang khách và quản trị

- `/`: trang xem nhà công khai, có ảnh, chi tiết, tìm kiếm, lọc loại và sắp xếp giá.
- Khách gửi đăng ký mua / đặt lịch ngay trên trang chủ, không cần tài khoản. Điền tên, CCCD, điện thoại, email; lịch xem cần ngày giờ tương lai.
- Yêu cầu mới luôn chờ duyệt / chờ xác nhận. Chỉ admin được xem hồ sơ khách và xử lý yêu cầu.
- Góc trên có Đăng nhập quản trị → `/login`; đăng nhập bằng `ADMIN_USER` và `ADMIN_PASSWORD` đã đặt trên Render.
- `/housing/dashboard`, `/housing/**` và `/api/**` chỉ dành cho quyền ADMIN. Dữ liệu cá nhân không gửi xuống trang công khai.
- Giữ nguyên cấu trúc PostgreSQL và cấu hình Render; không cần tạo lại database.
