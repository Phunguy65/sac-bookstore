# 2. Đăng ký

## Mô tả

Người dùng điền đầy đủ thông tin đăng ký gồm email, mật khẩu (kèm xác nhận), họ tên và số điện thoại để tạo tài khoản khách hàng mới. Hệ thống kiểm tra email chưa được sử dụng và mật khẩu xác nhận khớp với mật khẩu gốc. Nếu hợp lệ, tài khoản được tạo ở trạng thái ACTIVE và người dùng được chuyển hướng đến trang đăng nhập kèm thông báo thành công.

## Bảng mô tả use case

| Thuộc tính        | Nội dung                                                                          |
|-------------------|-----------------------------------------------------------------------------------|
| Mã                | UC-02                                                                             |
| Tên               | Đăng ký                                                                           |
| Tác nhân         | Khách hàng (Customer)                                                             |
| Mô tả            | Khách hàng cung cấp thông tin cá nhân để tạo tài khoản mới trong hệ thống        |
| Điều kiện tiên   | Người dùng chưa đăng nhập (chưa có session hợp lệ)                              |
| Kết quả           | Tài khoản mới được tạo, người dùng được chuyển đến trang đăng nhập                |

## Sequence Diagram

```plantuml
@startuml
skinparam backgroundColor #FEFEFE

actor "Khách hàng" as Customer
participant "RegisterPageBean" as Page #skyblue
participant "AuthApplicationService" as AuthService #lightgreen
participant "CustomerRepository" as CustomerRepo #lightyellow
participant "PasswordHasher" as PasswordHasher #lightgray
participant "AuthSession" as Session #lightpink

Customer -> Page: Gửi form đăng ký\n(email, password, confirmPassword, fullName, phoneNumber)
activate Page

alt Xác nhận mật khẩu không khớp
    Page -> Customer: Hiển thị "Xac nhan mat khau khong khop."
    deactivate Page
else Mật khẩu xác nhận khớp
    Page -> AuthService: register(email, password, confirmPassword, fullName, phoneNumber)
    activate AuthService

    AuthService -> CustomerRepo: findByEmail(email)
    activate CustomerRepo
    CustomerRepo --> AuthService: Optional<Customer>
    deactivate CustomerRepo

    alt Email đã được sử dụng
        AuthService --> Page: RegisterResult.failure("Email da duoc su dung.")
    else Email chưa tồn tại
        AuthService -> PasswordHasher: hash(password)
        activate PasswordHasher
        PasswordHasher --> AuthService: passwordHash
        deactivate PasswordHasher

        AuthService -> CustomerRepo: save(Customer)
        activate CustomerRepo
        CustomerRepo --> AuthService: Customer (đã lưu)
        deactivate CustomerRepo

        AuthService --> Page: RegisterResult.success(customer)
    end

    deactivate AuthService

    alt Đăng ký thành công
        Page -> Customer: Redirect → /auth/login.jsp?registered=1
    else Đăng ký thất bại
        Page -> Customer: Hiển thị thông báo lỗi\n+ Giữ ở /auth/register.jsp\n+ Giữ lại các trường đã nhập
    end
end

deactivate Page

@enduml
```
<!-- docs/images/usecase/uc-02.svg -->
![skinparam](docs/images/usecase/uc-02.svg)





## Exception Flows

| Exception                                | Thông báo cho người dùng                   | Hành vi hệ thống                |
|------------------------------------------|---------------------------------------------|-----------------------------------|
| Mật khẩu xác nhận không khớp             | "Xac nhan mat khau khong khop."            | Giữ ở trang đăng ký, hiển thị lỗi |
| Email đã được sử dụng                  | "Email da duoc su dung."                   | Giữ ở trang đăng ký, hiển thị lỗi |
| Email không hợp lệ (IllegalArgumentException) | Thông báo lỗi validation từ Email value object | Giữ ở trang đăng ký, hiển thị lỗi |
| Lỗi hệ thống (RuntimeException)        | "Dang ky that bai. Vui long thu lai sau." | Giữ ở trang đăng ký, hiển thị lỗi |
