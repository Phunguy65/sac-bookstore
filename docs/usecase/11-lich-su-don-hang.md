# 11. Xem lịch sử đơn hàng

## Mô tả

Khách hàng truy cập trang lịch sử đơn hàng để xem toàn bộ các đơn hàng đã đặt. Hệ thống truy vấn danh sách đơn hàng theo khách hàng từ cơ sở dữ liệu, sắp xếp theo thời gian đặt mới nhất, rồi hiển thị dạng tóm tắt gồm mã đơn, trạng thái, tổng tiền và thời gian. Trang hỗ trợ phân trang để giới hạn số đơn hiển thị trên mỗi trang.

## Bảng mô tả use case

| Thuộc tính        | Nội dung                                                                    |
|-------------------|-----------------------------------------------------------------------------|
| Mã                | UC-11                                                                       |
| Tên               | Xem lịch sử đơn hàng                                                        |
| Tác nhân         | Khách hàng (Customer)                                                       |
| Mô tả            | Khách hàng xem danh sách tóm tắt các đơn hàng đã đặt, có phân trang         |
| Điều kiện tiên   | Khách hàng đã đăng nhập                                                    |
| Kết quả           | Trang hiển thị danh sách đơn hàng đã đặt (tóm tắt, có phân trang)         |

## Sequence Diagram

```plantuml
@startuml
skinparam backgroundColor #FEFEFE

actor "Khách hàng" as Customer
participant "OrderHistoryPageBean" as Page #skyblue
participant "OrderQueryApplicationService" as OrderService #lightgreen
participant "OrderRepository" as OrderRepo #lightyellow

Customer -> Page: Truy cập GET /account/orders.jsp\n(AuthGuard đã xác minh session)
activate Page

alt Có tham số error=missing
    Page -> Page: Set errorMessage = "Khong tim thay don hang phu hop."
end

Page -> OrderService: getOrderHistory(customerId)
activate OrderService

OrderService -> OrderRepo: findByCustomerId(customerId)
activate OrderRepo
OrderRepo --> OrderService: List<Order>
deactivate OrderRepo

loop Mỗi Order
    OrderService -> OrderService: OrderSummaryView
    note right
        Tạo OrderSummaryView:
        - orderId
        - status (OrderStatus enum)
        - totalAmount (Money)
        - itemCount (order.items().size())
        - placedAt (thời gian đặt)
    end
end

OrderService --> Page: List<OrderSummaryView>
deactivate OrderService

Page -> Page: Phân trang
note right
    PAGE_SIZE = 10 đơn / trang
    Tính totalPages = ceil(total / 10)
    Lấy subList(fromIndex, toIndex)
end

alt Có tham số ?error=missing
    Page -> Customer: Render /account/orders.jsp\n+ Danh sách OrderSummaryView (trang hiện tại)\n+ Thông báo lỗi "Khong tim thay don hang phu hop."
else Không có lỗi
    Page -> Customer: Render /account/orders.jsp\n+ Danh sách OrderSummaryView (trang hiện tại)\n+ Phân trang
end

deactivate Page

@enduml
```
<!-- docs/images/usecase/uc-11.svg -->
![skinparam](docs/images/usecase/uc-11.svg)





## Exception Flows

| Exception                                  | Thông báo cho người dùng                              | Hành vi hệ thống              |
|--------------------------------------------|------------------------------------------------------|-------------------------------|
| Không có đơn hàng nào                     | Trang trống (empty state)                          | Hiển thị trang không có đơn  |
| Lỗi khi truy vấn cơ sở dữ liệu          | "Đã xảy ra lỗi khi tải lịch sử đơn hàng."       | Hiển thị thông báo lỗi chung |

## Chi tiết phân trang

```
Tổng số đơn hàng: 25
PAGE_SIZE: 10 đơn / trang

Trang 1: Đơn 1-10    ← Trang mặc định khi không có tham số page
Trang 2: Đơn 11-20
Trang 3: Đơn 21-25

URL: /account/orders.jsp?page=2

Validation:
- page < 1        → Hiển thị trang 1
- page > totalPages → Hiển thị trang cuối
- page không hợp lệ → Hiển thị trang 1
```

## Chi tiết thông tin tóm tắt đơn hàng

Mỗi dòng trong lịch sử đơn hàng hiển thị:

| Trường          | Nguồn dữ liệu         |
|-----------------|----------------------|
| Mã đơn hàng     | `Order.id`          |
| Trạng thái      | `Order.status`      |
| Tổng tiền       | `Order.totalAmount`  |
| Số loại sách    | `order.items().size()` |
| Ngày đặt        | `Order.placedAt`    |