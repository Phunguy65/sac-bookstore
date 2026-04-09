# UC-07: Xem lịch sử đơn hàng

## 1. Mô tả use case

| Mục                            | Nội dung                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |
| ------------------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| Phụ thuộc                      | UC-06 (Đặt hàng) — khách hàng phải đã đặt ít nhất một đơn hàng để danh sách không rỗng.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| Mục đích                       | Khách hàng muốn xem lại toàn bộ đơn hàng đã đặt để theo dõi trạng thái. PM giúp truy vấn danh sách đơn hàng, sắp xếp theo thời gian mới nhất, phân trang, và hiển thị tóm tắt mỗi đơn.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
| Mô tả                          | Khách hàng truy cập trang lịch sử đơn hàng. Hệ thống truy vấn toàn bộ đơn hàng của khách, sắp xếp theo createdAt giảm dần, phân trang in-memory (10 đơn/trang), và hiển thị dạng tóm tắt gồm mã đơn, trạng thái, tổng tiền, số loại sách, thời gian đặt.                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |
| Actor chính                    | Khách hàng (Customer)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
| Actor liên quan                | Không                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
| Tiền điều kiện                 | Khách hàng đã truy cập vào hệ thống (có session hợp lệ).                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |
| Dãy lệnh thực hiện bình thường | 1. Khách hàng truy cập GET /account/orders.jsp (có thể kèm ?page=N). <br> 2. Hệ thống kiểm tra errorParam: nếu errorParam="missing" → set errorMessage = "Khong tim thay don hang phu hop." <br> 3. Hệ thống truy vấn toàn bộ đơn hàng của khách, sắp xếp theo createdAt DESC. <br> 4. Hệ thống chuyển đổi mỗi Order → OrderSummaryView (orderId, status, totalAmount, itemCount, placedAt). <br> 5. Hệ thống tính phân trang: totalPages = ceil(size / 10), parsePage clamp pageParam vào [1, totalPages]. <br> 6. Hệ thống cắt subList(fromIndex, toIndex) cho trang hiện tại. <br> 7. Hệ thống render trang lịch sử với danh sách OrderSummaryView, errorMessage (nếu có), currentPage, totalPages, hasPreviousPage, hasNextPage. |
| Hậu điều kiện (thành công)     | Trang hiển thị danh sách đơn hàng tóm tắt (trang hiện tại), có nút phân trang nếu nhiều hơn 1 trang.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
| Hậu điều kiện (thất bại)       | Không áp dụng — UC này luôn RENDER (không REDIRECT). Nếu không có đơn hàng, hiển thị trang trống (empty state). Nếu lỗi DB, hiển thị thông báo lỗi chung.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
| Xử lý ngoại lệ                 | errorParam="missing" (từ UC-08 redirect) → hiển thị thông báo "Khong tim thay don hang phu hop." cùng danh sách đơn <br> Không có đơn hàng nào → trang trống (empty state) <br> pageParam không hợp lệ (NaN, < 1, > totalPages) → clamp về trang 1 hoặc trang cuối <br> Lỗi truy vấn cơ sở dữ liệu → thông báo lỗi chung                                                                                                                                                                                                                                                                                                                                                                                                             |

## 2. Lược đồ tuần tự

<!-- Lược đồ cấp 1: Actor ↔ PM (hệ thống là hộp đen). -->

```plantuml
@startuml UC-07
title UC-07: Xem lịch sử đơn hàng

actor "Khách hàng" as Actor
participant "Hệ thống" as API

Actor -> API: GET /account/orders.jsp(page?, error?)
alt Lỗi truy vấn cơ sở dữ liệu
    API --> Actor: 500 + ERROR_DATABASE
else Có errorParam="missing"
    API -> API: truy vấn đơn hàng, phân trang
    API --> Actor: 200 + OrderHistoryPageModel(orders, "Khong tim thay don hang phu hop.", currentPage, totalPages)
else Không có đơn hàng
    API --> Actor: 200 + OrderHistoryPageModel(emptyList, null, 1, 1)
else Có đơn hàng
    API -> API: truy vấn đơn hàng ORDER BY createdAt DESC, phân trang (PAGE_SIZE=10)
    API --> Actor: 200 + OrderHistoryPageModel(List<OrderSummaryView>, null, currentPage, totalPages)
end
@enduml
```

## 3. Lược đồ hoạt động

```plantuml
@startuml UC-07-activity
title UC-07: Xem lịch sử đơn hàng - Activity Diagram

start

:Khách hàng truy cập GET /account/orders.jsp
(pageParam, errorParam);

if (errorParam == "missing"?) then (có)
  :errorMessage = "Khong tim thay don hang phu hop.";
else (không)
  :errorMessage = null;
endif

:Truy vấn toàn bộ đơn hàng của khách
ORDER BY createdAt DESC;

if (Truy vấn thành công?) then (không)
  :Trả thông báo lỗi chung;
  stop
else (có)
endif

:Chuyển đổi mỗi Order → OrderSummaryView
(orderId, status, totalAmount,
itemCount = items.size(), placedAt);

:Tính phân trang:
totalPages = max(1, ceil(size / 10))
currentPage = parsePage(pageParam, totalPages)
  page < 1 → 1
  page > totalPages → totalPages
  NaN → 1;

:Cắt subList(fromIndex, toIndex)
cho trang hiện tại;

:Render trang lịch sử đơn hàng
OrderHistoryPageModel(orders, errorMessage,
currentPage, totalPages)
+ hasPreviousPage, hasNextPage;

stop
@enduml
```

## 5. Lược đồ lớp ý niệm

```plantuml
@startuml UC-07-class
title UC-07: Xem lịch sử đơn hàng - Conceptual Class Diagram

class "Order" as OrderEntity {
  - id: OrderId
  - customerId: CustomerId
  - status: OrderStatus
  - totalAmount: Money
  - shippingAddress: AddressDetails
  - items: List<OrderItem>
  - placedAt: Instant
  - cancelledAt: Instant
  - version: long
  - createdAt: Instant
  - updatedAt: Instant
}

class "OrderId" as OrderIdVO {
  - value: Long
}

class "CustomerId" as CustomerIdVO {
  - value: Long
}

class "OrderStatus" as StatusEnum {
  <<enumeration>>
  PLACED
}

class "Money" as MoneyVO {
  - amount: BigDecimal
}

class "OrderItem" as OrderItemEntity {
  - id: OrderItemId
  - bookId: BookId
  - bookTitleSnapshot: BookTitle
  - bookIsbnSnapshot: Isbn
  - unitPriceSnapshot: Money
  - quantity: Quantity
  - lineTotal: Money
  - createdAt: Instant
}

class "OrderSummaryView" as SummaryDTO {
  + orderId: OrderId
  + status: OrderStatus
  + totalAmount: Money
  + itemCount: int
  + placedAt: Instant
}

class "OrderHistoryPageModel" as PageModel {
  - orders: List<OrderSummaryView>
  - errorMessage: String
  - currentPage: int
  - totalPages: int
  + isHasPreviousPage(): boolean
  + isHasNextPage(): boolean
}

class "OrderHistoryPageRequest" as ReqDTO {
  + pageParam: String
  + errorParam: String
}

class "OrderHistoryPageResult" as ResDTO {
  + action: PageAction
  + model: OrderHistoryPageModel
}

OrderEntity *-- OrderIdVO
OrderEntity *-- CustomerIdVO
OrderEntity *-- StatusEnum
OrderEntity *-- MoneyVO
OrderEntity *-- "0..*" OrderItemEntity
SummaryDTO o-- OrderIdVO
SummaryDTO o-- StatusEnum
SummaryDTO o-- MoneyVO
PageModel o-- "0..*" SummaryDTO
ResDTO o-- PageModel
@enduml
```

## 6. Phân rã thành phần PM

### 6.1 Controller: `OrderHistoryPageBean`

- **Nhiệm vụ**: Nhận request từ JSP, xử lý errorParam, ủy thác truy vấn cho
  UseCase, thực hiện phân trang in-memory, trả về model để render.
- **Endpoint**: `GET /account/orders.jsp`
- **Input**: `OrderHistoryPageRequest` —
  `{ pageParam: String, errorParam: String }`
- **Output**: `OrderHistoryPageResult` —
  `{ action: PageAction.RENDER, model: OrderHistoryPageModel }`
- **Lưu ý**: Controller luôn trả RENDER, không bao giờ REDIRECT. Phân trang thực
  hiện tại controller (in-memory subList), không tại DB.

### 6.2 UseCase: `OrderQueryApplicationService.getOrderHistory()`

- **Nhiệm vụ**: Truy vấn toàn bộ đơn hàng của khách hàng, chuyển đổi sang
  OrderSummaryView.
- **Input**: `CustomerId`
- **Output**: `List<OrderSummaryView>`
- **Gọi đến**:
    - `OrderRepository.findByCustomerId(customerId)` — truy vấn đơn hàng theo
      khách, sắp xếp createdAt DESC
- **Logic**: map mỗi Order → OrderSummaryView(orderId, status, totalAmount,
  items.size(), placedAt).

### 6.3 Repository: `OrderJpaRepository`

- **Nhiệm vụ**: Truy vấn Order từ DB.
- **Phương thức liên quan đến UC**:
    - `findByCustomerId(customerId): List<Order>` — JPQL:
      `SELECT DISTINCT o FROM OrderEntity o LEFT JOIN FETCH o.items WHERE o.customer.id = :customerId ORDER BY o.createdAt DESC`
- **Table**: `orders`, `order_items`

### 6.5 Lược đồ tuần tự nội bộ PM

```plantuml
@startuml UC-07-internal
title UC-07: Xem lịch sử đơn hàng - Internal Sequence

actor "Khách hàng" as Actor
participant "OrderHistoryPageBean" as CTL
participant "OrderQueryApplicationService" as UC
participant "OrderJpaRepository" as REPO
database "DB (orders, order_items)" as DB

Actor -> CTL: GET /account/orders.jsp (pageParam, errorParam)

CTL -> CTL: errorParam == "missing"?\n→ errorMessage = "Khong tim thay don hang phu hop."

CTL -> UC: getOrderHistory(CustomerId.DEFAULT_CUSTOMER)
UC -> REPO: findByCustomerId(customerId)
REPO -> DB: SELECT DISTINCT o FROM OrderEntity o\nLEFT JOIN FETCH o.items\nWHERE o.customer.id = :customerId\nORDER BY o.createdAt DESC
DB --> REPO: List<OrderEntity>
REPO --> UC: List<Order>

loop Mỗi Order
    UC -> UC: new OrderSummaryView(\n  order.id(), order.status(),\n  order.totalAmount(),\n  order.items().size(),\n  order.placedAt())
end

UC --> CTL: List<OrderSummaryView>

CTL -> CTL: totalPages = max(1, ceil(size / 10))
CTL -> CTL: currentPage = parsePage(pageParam, totalPages)\n  NaN → 1, < 1 → 1, > totalPages → totalPages
CTL -> CTL: subList(fromIndex, toIndex)
CTL -> CTL: new OrderHistoryPageModel(\n  orders, errorMessage,\n  currentPage, totalPages)

CTL --> Actor: OrderHistoryPageResult(\n  RENDER, model)
@enduml
```

## 7. Bảng tham chiếu dò vết

| Use Case | Controller           | Endpoint                  | UseCase                                        | Repository                            | Table               |
| -------- | -------------------- | ------------------------- | ---------------------------------------------- | ------------------------------------- | ------------------- |
| UC-07    | OrderHistoryPageBean | `GET /account/orders.jsp` | OrderQueryApplicationService.getOrderHistory() | OrderJpaRepository.findByCustomerId() | orders, order_items |

## 8. Tiêu chí kiểm thử

| Tiêu chí                       | Phép thử                                                                   | Kết quả mong đợi                                                                                                                                | Ghi chú                                                                            |
| ------------------------------ | -------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------- | ---------------------------------------------------------------------------------- |
| Toàn diện (coverage)           | Đối chiếu Activity Diagram ↔ Sequence Diagram: mọi luồng đều được thể hiện | Không bỏ sót luồng chính lẫn ngoại lệ                                                                                                           | Rà soát chéo giữa mục 2 và mục 3                                                   |
| Nhất quán                      | Rà soát tên lớp, DTO, API giữa các lược đồ trong cùng UC                   | Không mâu thuẫn giữa các mục 2–6                                                                                                                | Đặc biệt kiểm tra tên trong mục 5–6                                                |
| Truy vết                       | Đối chiếu bảng tham chiếu (mục 7) với lược đồ tuần tự nội bộ (mục 6.5)     | Mọi tương tác trong sequence đều có entry                                                                                                       | Kiểm tra không thiếu endpoint/method                                               |
| errorParam mapping             | Gọi handle(new OrderHistoryPageRequest(null, "missing"))                   | model.getErrorMessage() == "Khong tim thay don hang phu hop." VÀ orders vẫn hiển thị                                                            | Test: PurchasePageBeansTest.orderHistoryPageMapsMissingErrorParam                  |
| Danh sách rỗng                 | getOrderHistory khi không có đơn nào                                       | Trả List rỗng, trang hiển thị empty state, totalPages = 1, currentPage = 1                                                                      |                                                                                    |
| Phân trang — trang hợp lệ      | 25 đơn hàng, page=2                                                        | Hiển thị đơn 11-20, currentPage=2, totalPages=3, hasPreviousPage=true, hasNextPage=true                                                         |                                                                                    |
| Phân trang — page < 1          | page=0 hoặc page=-1                                                        | Clamp về currentPage=1                                                                                                                          | parsePage logic                                                                    |
| Phân trang — page > totalPages | 5 đơn (1 trang), page=99                                                   | Clamp về currentPage=1 (= totalPages)                                                                                                           | parsePage logic                                                                    |
| Phân trang — page NaN          | page="abc"                                                                 | Clamp về currentPage=1                                                                                                                          | parsePage NumberFormatException                                                    |
| Phân trang — page null         | Không truyền page                                                          | currentPage=1 (default)                                                                                                                         | parsePage("1")                                                                     |
| Sắp xếp                        | Nhiều đơn hàng với createdAt khác nhau                                     | Đơn mới nhất hiển thị trước (createdAt DESC)                                                                                                    | Xác minh ORDER BY trong JPQL                                                       |
| Chỉ đơn của khách              | Khách A có 2 đơn, khách B có 1 đơn                                         | getOrderHistory(A) trả 2, getOrderHistory(B) trả 1                                                                                              | Test: OrderQueryApplicationServiceTest.getOrderHistoryReturnsOnlyOrdersForCustomer |
| OrderSummaryView fields        | Kiểm tra mapping Order → OrderSummaryView                                  | orderId = order.id(), status = order.status(), totalAmount = order.totalAmount(), itemCount = order.items().size(), placedAt = order.placedAt() | Kiểm tra trong OrderQueryApplicationService                                        |
