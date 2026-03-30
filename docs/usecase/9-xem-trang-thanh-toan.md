# 9. Xem trang thanh toán

## Mô tả

Khách hàng truy cập trang thanh toán để xem lại giỏ hàng và chuẩn bị đặt hàng. Hệ thống lấy thông tin giỏ hàng, kết hợp với địa chỉ giao hàng mặc định đã lưu (nếu có). Nếu giỏ hàng trống, hệ thống chuyển hướng ngay về trang giỏ hàng kèm thông báo. Người dùng có thể điền địa chỉ giao hàng mới trực tiếp trên trang nếu chưa có địa chỉ mặc định.

## Bảng mô tả use case

| Thuộc tính        | Nội dung                                                                    |
|-------------------|-----------------------------------------------------------------------------|
| Mã                | UC-09                                                                       |
| Tên               | Xem trang thanh toán                                                        |
| Tác nhân         | Khách hàng (Customer)                                                       |
| Mô tả            | Khách hàng xem lại giỏ hàng và chuẩn bị thông tin đặt hàng                 |
| Điều kiện tiên   | Khách hàng đã đăng nhập, có ít nhất một sách trong giỏ                    |
| Kết quả           | Trang hiển thị giỏ hàng, địa chỉ giao hàng (có sẵn hoặc form nhập mới)  |

## Sequence Diagram

```plantuml
@startuml
skinparam backgroundColor #FEFEFE

actor "Khách hàng" as Customer
participant "CheckoutPageBean" as Page #skyblue
participant "CheckoutApplicationService" as CheckoutService #lightgreen
participant "CartApplicationService" as CartService #lightgreen
participant "CartViewAssembler" as Assembler #lightgreen
participant "AddressRepository" as AddressRepo #lightyellow
participant "CartRepository" as CartRepo #lightyellow
participant "BookRepository" as BookRepo #lightyellow

Customer -> Page: Truy cập GET /account/checkout.jsp\n(AuthGuard đã xác minh session)
activate Page

Page -> CheckoutService: getCheckout(customerId)
activate CheckoutService

CheckoutService -> CartService: buildCartView(customerId)
activate CartService

CartService -> CartRepo: findByCustomerId(customerId)
activate CartRepo
CartRepo --> CartService: Optional<Cart>
deactivate CartRepo

alt Không có Cart
    CartService -> Cart: new Cart(null, customerId, List.of(), ...)
end

CartService -> Assembler: toCartView(cart, bookRepository)
activate Assembler
activate BookRepo

loop Mỗi CartItem
    Assembler -> BookRepo: findById(cartItem.bookId)
    BookRepo --> Assembler: Book
    Assembler -> Assembler: CartLineView
end

Assembler --> CartService: CartView
deactivate Assembler
deactivate BookRepo

CartService --> CheckoutService: CartView
deactivate CartService

CheckoutService -> AddressRepo: findDefaultByCustomerId(customerId)
activate AddressRepo
AddressRepo --> CheckoutService: Optional<Address>
deactivate AddressRepo

alt Có địa chỉ mặc định
    CheckoutService -> CheckoutService: CheckoutView\n(cart, addressDetails, requiresInput=false)
else Không có địa chỉ mặc định
    CheckoutService -> CheckoutService: CheckoutView\n(cart, null, requiresInput=true)
end

CheckoutService --> Page: CheckoutView
deactivate CheckoutService

alt CartView trống
    Page -> Customer: Redirect → /account/cart.jsp?info=emptyCheckout
    note right
        CheckoutPageBean kiểm tra isEmpty()
        TRƯỚC KHI hiển thị trang.
        Nếu giỏ trống → redirect ngay lập tức.
    end
else CartView có hàng
    alt Có địa chỉ mặc định
        Page -> Customer: Render /account/checkout.jsp\n+ Hiển thị giỏ hàng\n+ Hiển thị địa chỉ giao hàng (read-only)\n+ Nút "Đặt hàng"
    else Không có địa chỉ
        Page -> Customer: Render /account/checkout.jsp\n+ Hiển thị giỏ hàng\n+ Hiển thị form nhập địa chỉ giao hàng\n+ Nút "Đặt hàng"
    end
end

deactivate Page

@enduml
```
<!-- docs/images/usecase/uc-09.svg -->
![skinparam](docs/images/usecase/uc-09.svg)





## Exception Flows

| Exception                                  | Thông báo cho người dùng                                              | Hành vi hệ thống                         |
|--------------------------------------------|-----------------------------------------------------------------------|------------------------------------------|
| Giỏ hàng trống                            | "Gio hang dang trong. Vui long them sach truoc khi thanh toan."      | Redirect về /account/cart.jsp?info=emptyCheckout |
| Lỗi khi truy vấn cơ sở dữ liệu          | "Đã xảy ra lỗi khi tải thông tin thanh toán."                      | Hiển thị thông báo lỗi chung           |

## Chi tiết địa chỉ giao hàng

Trang thanh toán có hai trường hợp hiển thị địa chỉ:

```
┌─────────────────────────────────────────────────────────┐
│ TRƯỜNG HỢP 1: Có địa chỉ mặc định                     │
│ - Hiển thị địa chỉ dạng read-only                      │
│ - Người dùng không thể sửa địa chỉ tại đây             │
│ - requiresShippingAddressInput = false                   │
├─────────────────────────────────────────────────────────┤
│ TRƯỜNG HỢP 2: Không có địa chỉ mặc định               │
│ - Hiển thị form đầy đủ 8 trường địa chỉ               │
│ - Người dùng BẮT BUỘC nhập địa chỉ giao hàng          │
│ - requiresShippingAddressInput = true                    │
│ - recipientName, phoneNumber, line1, ward,              │
│   district, city, province, postalCode                 │
│   (line2 là tùy chọn)                                  │
└─────────────────────────────────────────────────────────┘
```