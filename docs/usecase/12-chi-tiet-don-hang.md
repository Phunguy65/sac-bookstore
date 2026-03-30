# 12. Xem chi tiết đơn hàng

## Mô tả

Khách hàng xem chi tiết một đơn hàng cụ thể bằng cách truy cập trang chi tiết kèm mã đơn hàng. Hệ thống kiểm tra đơn hàng thuộc về khách hàng hiện tại (để đảm bảo bảo mật — không ai có thể xem đơn của người khác). Nếu mã đơn không hợp lệ hoặc đơn không thuộc về khách hàng, hệ thống chuyển hướng về trang lịch sử đơn hàng.

## Bảng mô tả use case

| Thuộc tính        | Nội dung                                                                    |
|-------------------|-----------------------------------------------------------------------------|
| Mã                | UC-12                                                                       |
| Tên               | Xem chi tiết đơn hàng                                                       |
| Tác nhân         | Khách hàng (Customer)                                                       |
| Mô tả            | Khách hàng xem thông tin chi tiết một đơn hàng cụ thể mà mình đã đặt       |
| Điều kiện tiên   | Khách hàng đã đăng nhập                                                    |
| Kết quả           | Trang hiển thị chi tiết đầy đủ đơn hàng hoặc chuyển hướng nếu không tìm thấy |

## Sequence Diagram

```plantuml
@startuml
skinparam backgroundColor #FEFEFE

actor "Khách hàng" as Customer
participant "OrderDetailPageBean" as Page #skyblue
participant "OrderQueryApplicationService" as OrderService #lightgreen
participant "OrderRepository" as OrderRepo #lightyellow

Customer -> Page: Truy cập GET /account/order-detail.jsp?orderId={id}\n(AuthGuard đã xác minh session)
activate Page

alt orderId không hợp lệ (null, rỗng, <= 0)
    Page -> Customer: Redirect → /account/orders.jsp?error=missing
    note right
        parseOrderId trả về -1
        cho các trường hợp:
        - orderId = null
        - orderId = ""
        - orderId không phải số
    end
else orderId hợp lệ
    Page -> OrderService: getOwnedOrder(customerId, orderId)
    activate OrderService

    OrderService -> OrderRepo: findByCustomerIdAndId(customerId, orderId)
    activate OrderRepo
    note right
        TRUY VẤN QUAN TRỌNG:
        Tìm đơn hàng với ĐỒNG THỜI
        customerId + orderId
        → Đảm bảo khách hàng sở hữu đơn này
        → Ngăn chặn xem đơn của người khác
    end
    OrderRepo --> OrderService: Optional<Order>
    deactivate OrderRepo

    alt Không tìm thấy đơn hàng
        OrderService --> Page: OrderLookupResult.notFound("Khong tim thay don hang phu hop.")
        Page -> Customer: Redirect → /account/orders.jsp?error=missing
    else Tìm thấy đơn hàng
        OrderService -> OrderService: OrderDetailView
        note right
            Tạo OrderDetailView:
            - orderId, status
            - totalAmount
            - OrderAddressView (shipping address)
              recipientName, phoneNumber
              line1, line2, ward, district
              city, province, postalCode
            - List<OrderItemDetailView>
              bookId, bookTitleSnapshot
              bookIsbnSnapshot, quantity, lineTotal
        end
        OrderService --> Page: OrderLookupResult.found(orderDetailView)
        Page -> Customer: Render /account/order-detail.jsp\nvới OrderDetailView
    end

    deactivate OrderService
end

deactivate Page

@enduml
```
<!-- docs/images/usecase/uc-12.svg -->
![skinparam](docs/images/usecase/uc-12.svg)





## Exception Flows

| Exception                                  | Thông báo cho người dùng                              | Hành vi hệ thống                            |
|--------------------------------------------|------------------------------------------------------|---------------------------------------------|
| orderId không hợp lệ (null, rỗng, <= 0)   | —                                                    | Redirect → /account/orders.jsp?error=missing |
| Đơn hàng không tồn tại                    | —                                                    | Redirect → /account/orders.jsp?error=missing |
| Đơn hàng không thuộc về khách hàng hiện tại | —                                              | Redirect → /account/orders.jsp?error=missing |

## Chi tiết bảo mật — Ownership Check

Đây là điểm bảo mật quan trọng nhất của use case này:

```
┌──────────────────────────────────────────────────────────┐
│  TRUY VẤN: findByCustomerIdAndId(customerId, orderId)  │
│                                                          │
│  Tác dụng:                                               │
│  - Đảm bảo đơn hàng TỒN TẠI trong DB                   │
│  - Đảm bảo đơn hàng THUỘC VỀ khách hàng hiện tại     │
│                                                          │
│  Kết quả:                                                │
│  - Tìm thấy → Hiển thị chi tiết                        │
│  - Không tìm thấy → Redirect về orders + error=missing  │
│                                                          │
│  → Ngăn chặn hoàn toàn việc xem đơn của người khác      │
└──────────────────────────────────────────────────────────┘
```

## Chi tiết thông tin chi tiết đơn hàng

Trang chi tiết hiển thị đầy đủ thông tin:

```
THÔNG TIN ĐƠN HÀNG
├─ Mã đơn hàng: #123
├─ Trạng thái: PLACED
└─ Tổng tiền: 300.000 VND

ĐỊA CHỈ GIAO HÀNG (snapshot tại thời điểm đặt)
├─ Người nhận: Nguyễn Văn A
├─ Số điện thoại: 0912xxxxxx
├─ Địa chỉ: 123 Đường ABC, Phường X
│             Quận Y, TP Z, Tỉnh T
└─ Mã bưu chính: 70000

DANH SÁCH SẢN PHẨM (từ order_items)
├─ Sách A × 2    |  80.000đ  |  160.000đ
├─ Sách B × 1    | 140.000đ  |  140.000đ
└─ TỔNG CỘNG                             300.000đ
```

Tất cả thông tin sách trong đơn là **snapshot tại thời điểm đặt hàng**, đảm bảo tính chính xác lịch sử.