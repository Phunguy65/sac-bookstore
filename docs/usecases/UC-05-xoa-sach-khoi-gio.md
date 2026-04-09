# UC-05: Xóa sách khỏi giỏ

## 1. Mô tả use case

| Mục                            | Nội dung                                                                                                                                                                                                                                                                                                                                                                                      |
| ------------------------------ | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Phụ thuộc                      | UC-03 (Xem giỏ hàng) — khách hàng phải đang ở trang giỏ hàng để thực hiện xóa sách.                                                                                                                                                                                                                                                                                                           |
| Mục đích                       | Khách hàng muốn bỏ một cuốn sách không còn muốn mua ra khỏi giỏ. PM giúp lọc bỏ CartItem tương ứng và lưu giỏ hàng cập nhật. Hành vi là idempotent — xóa sách không có trong giỏ vẫn thành công.                                                                                                                                                                                              |
| Mô tả                          | Khách hàng chọn xóa một cuốn sách khỏi giỏ hàng. Hệ thống tìm giỏ hàng, lọc bỏ dòng có bookId tương ứng, rồi lưu lại giỏ hàng đã cập nhật. Trang làm mới để hiển thị kết quả.                                                                                                                                                                                                                 |
| Actor chính                    | Khách hàng (Customer)                                                                                                                                                                                                                                                                                                                                                                         |
| Actor liên quan                | Không                                                                                                                                                                                                                                                                                                                                                                                         |
| Tiền điều kiện                 | Khách hàng đã truy cập vào hệ thống (có session hợp lệ), đang ở trang giỏ hàng.                                                                                                                                                                                                                                                                                                               |
| Dãy lệnh thực hiện bình thường | 1. Khách hàng nhấn nút xóa cho một cuốn sách (POST /account/cart.jsp với action=remove, bookId). <br> 2. Hệ thống tìm giỏ hàng của khách hàng (hoặc tạo giỏ rỗng nếu chưa có). <br> 3. Hệ thống lọc bỏ CartItem có bookId tương ứng khỏi danh sách items. <br> 4. Hệ thống lưu giỏ hàng cập nhật. <br> 5. Hệ thống hiển thị thông báo "Gio hang da duoc cap nhat." và refresh trang giỏ hàng. |
| Hậu điều kiện (thành công)     | CartItem có bookId tương ứng bị loại khỏi giỏ hàng. Giỏ hàng đã lưu trong DB. Nếu sách không có trong giỏ, giỏ vẫn được lưu lại (no-op hợp lệ).                                                                                                                                                                                                                                               |
| Hậu điều kiện (thất bại)       | Giỏ hàng không thay đổi. Trang hiển thị thông báo lỗi.                                                                                                                                                                                                                                                                                                                                        |
| Xử lý ngoại lệ                 | bookId không phải số → "Ma sach khong hop le." (lỗi đầu trang) <br> Sách không có trong giỏ hàng → Không hiển thị lỗi, coi là thành công (no-op hợp lệ — kết quả cuối cùng đúng mong đợi) <br> Giỏ hàng không tồn tại → Tạo giỏ rỗng, lưu (no-op)                                                                                                                                             |

## 2. Lược đồ tuần tự

<!-- Lược đồ cấp 1: Actor ↔ PM (hệ thống là hộp đen). -->

```plantuml
@startuml UC-05
title UC-05: Xóa sách khỏi giỏ

actor "Khách hàng" as Actor
participant "Hệ thống" as API

Actor -> API: POST /account/cart.jsp(action=remove, bookId)
alt bookId không phải số
    API --> Actor: 200 + Error("Ma sach khong hop le.")
else Thành công (kể cả khi sách không có trong giỏ)
    API -> API: tìm giỏ, lọc bỏ CartItem theo bookId, lưu giỏ
    API --> Actor: 200 + Success("Gio hang da duoc cap nhat.")
end
@enduml
```

## 3. Lược đồ hoạt động

```plantuml
@startuml UC-05-activity
title UC-05: Xóa sách khỏi giỏ - Activity Diagram

start

:Khách hàng POST /account/cart.jsp
(action=remove, bookId);

if (bookId là số hợp lệ?) then (không)
  :Trả Error đầu trang
  "Ma sach khong hop le.";
  stop
else (có)
endif

:Tìm giỏ hàng theo customerId;

if (Giỏ hàng tồn tại?) then (không)
  :Tạo giỏ rỗng tạm thời
  (id=null, items=List.of());
else (có)
endif

:Lọc bỏ CartItem có bookId tương ứng
(items.stream()
  .filter(!item.bookId().equals(bookId))
  .toList());

note right
  Nếu bookId không có trong giỏ,
  danh sách items không thay đổi
  → no-op hợp lệ, vẫn save
end note

:Lưu giỏ hàng cập nhật;

:Hiển thị "Gio hang da duoc cap nhat."
+ Refresh trang giỏ hàng;

stop
@enduml
```

## 4. Lược đồ trạng thái

```plantuml
@startuml UC-05-state
title UC-05: Xóa sách khỏi giỏ - Cart State Diagram

[*] --> ChuaCo : Khách hàng chưa có giỏ
ChuaCo --> Trong : removeBook()\n(tạo giỏ rỗng, lưu — no-op)

[*] --> Trong : Giỏ tồn tại nhưng rỗng
Trong --> Trong : removeBook()\n(no-op, giỏ vẫn rỗng)

[*] --> CoHang : Giỏ có ít nhất 1 CartItem
CoHang --> CoHang : removeBook() khi giỏ còn > 1 item\n(xóa 1 CartItem, giỏ vẫn có hàng)
CoHang --> Trong : removeBook() khi giỏ chỉ còn 1 item\n(xóa CartItem cuối, giỏ trống)

note right of CoHang
  removeBook() luôn trả success
  kể cả khi bookId không có trong giỏ
  (idempotent / no-op behavior)
end note
@enduml
```

## 5. Lược đồ lớp ý niệm

```plantuml
@startuml UC-05-class
title UC-05: Xóa sách khỏi giỏ - Conceptual Class Diagram

class "Cart" as CartEntity {
  - id: CartId
  - customerId: CustomerId
  - items: List<CartItem>
  - version: long
  - createdAt: Instant
  - updatedAt: Instant
}

class "CartItem" as CartItemEntity {
  - id: CartItemId
  - bookId: BookId
  - quantity: Quantity
  - unitPriceSnapshot: Money
  - version: long
  - createdAt: Instant
  - updatedAt: Instant
}

class "CartActionResult" as ResultDTO {
  + success: boolean
  + errorMessage: String
  + fieldErrors: Map<String, String>
  + success(): CartActionResult
  + failure(msg): CartActionResult
}

class "BookId" as BookIdVO {
  - value: Long
}

class "CustomerId" as CustIdVO {
  - value: Long
}

CartEntity "1" *-- "0..*" CartItemEntity : items
CartItemEntity --> BookIdVO : bookId
CartEntity --> CustIdVO : customerId
@enduml
```

## 6. Phân rã thành phần PM

### 6.1 Controller: `CartPageBean`

- **Nhiệm vụ**: Nhận HTTP POST request từ khách hàng (action=remove, bookId),
  parse bookId, ủy thác cho UseCase, xử lý kết quả, sau đó gọi getCart() để
  refresh trang.
- **Endpoint**: `POST /account/cart.jsp`
- **Input**: `CartPageRequest` —
  `{ method: "POST", action: "remove", bookId: String, quantity: null, infoParam: null }`
- **Output thành công**: `200` + `CartPageResult(RENDER, CartPageModel)` — model
  chứa CartView mới + infoMessage "Gio hang da duoc cap nhat."
- **Output lỗi**: `200` + `CartPageResult(RENDER, CartPageModel)` — model chứa
  CartView + errorMessage.

### 6.2 UseCase: `CartApplicationService`

- **Nhiệm vụ**: Orchestrate nghiệp vụ xóa sách khỏi giỏ hàng.
- **Input**: `CustomerId`, `bookIdValue: long`
- **Output**: `CartActionResult`
- **Gọi đến**:
    - `CartRepository.findByCustomerId(customerId)` — tìm giỏ hàng (hoặc tạo giỏ
      rỗng)
    - `cart.items().stream().filter(...)` — lọc bỏ CartItem theo bookId
    - `CartRepository.save(updatedCart)` — lưu giỏ hàng cập nhật

- **Phát sinh sự kiện**: Không.
- **Lưu ý**: Không gọi BookRepository — không cần kiểm tra sách tồn tại, active,
  hay stock.

### 6.3 Repository: `CartRepository`

**CartRepository** (impl: `CartJpaRepository`):

- **Nhiệm vụ**: Truy xuất/lưu trữ domain entity `Cart` kèm `CartItem`.
- **Phương thức liên quan đến UC**:
    - `findByCustomerId(CustomerId): Optional<Cart>` — tìm giỏ hàng của khách
      hàng (LEFT JOIN FETCH items).
    - `save(Cart): Cart` — lưu giỏ hàng (persist nếu mới, merge nếu đã tồn tại).
- **Tables**: `carts`, `cart_items`

### 6.5 Lược đồ tuần tự nội bộ PM

```plantuml
@startuml UC-05-internal
title UC-05: Xóa sách khỏi giỏ - Internal Sequence

actor "Khách hàng" as Actor
participant "CartPageBean" as CTL
participant "CartApplicationService" as UC
participant "CartJpaRepository" as CREPO
database "DB" as DB

Actor -> CTL: POST /account/cart.jsp\n(action=remove, bookId)
CTL -> CTL: parseLong(bookId)

alt parseLong thất bại
    CTL --> Actor: 200 + Render trang\n+ Lỗi "Ma sach khong hop le."
else parse thành công
    CTL -> UC: removeBook(customerId, bookId)

    UC -> UC: BookId(bookIdValue)
    UC -> CREPO: findByCustomerId(customerId)
    CREPO -> DB: SELECT FROM carts LEFT JOIN cart_items\nWHERE customer_id = ?
    DB --> CREPO: CartEntity | null
    CREPO --> UC: Optional<Cart>

    alt Không có giỏ hàng
        UC -> UC: emptyCart(customerId)
    end

    UC -> UC: cart.items().stream()\n.filter(!item.bookId().equals(bookId))\n.toList()

    UC -> CREPO: save(copyCart(cart, filteredItems))
    CREPO -> DB: UPDATE carts + DELETE/UPDATE cart_items
    DB --> CREPO: CartEntity
    CREPO --> UC: Cart
    UC --> CTL: CartActionResult.success()

    CTL -> UC: getCart(customerId)
    UC --> CTL: CartView

    CTL --> Actor: 200 + Render trang\n"Gio hang da duoc cap nhat."\n+ CartView mới
end
@enduml
```

## 7. Bảng tham chiếu dò vết

| Use Case | Controller   | Endpoint                                 | UseCase                             | Repository                           | Table             |
| -------- | ------------ | ---------------------------------------- | ----------------------------------- | ------------------------------------ | ----------------- |
| UC-05    | CartPageBean | `POST /account/cart.jsp` (action=remove) | CartApplicationService.removeBook() | CartJpaRepository.findByCustomerId() | carts, cart_items |
|          |              |                                          |                                     | CartJpaRepository.save()             | carts, cart_items |
|          |              |                                          | CartApplicationService.getCart()    | CartJpaRepository.findByCustomerId() | carts, cart_items |
|          |              |                                          | CartViewAssembler.toCartView()      | BookJpaRepository.findByIds()        | books             |

## 8. Tiêu chí kiểm thử

| Tiêu chí                 | Phép thử                                                               | Kết quả mong đợi                                            | Ghi chú                                            |
| ------------------------ | ---------------------------------------------------------------------- | ----------------------------------------------------------- | -------------------------------------------------- |
| Toàn diện (coverage)     | Đối chiếu Activity ↔ Sequence: mọi luồng đều được thể hiện             | Không bỏ sót luồng chính lẫn ngoại lệ (parse lỗi, no-op)    | Rà soát chéo mục 2 và mục 3                        |
| Nhất quán                | Rà soát tên lớp, API giữa các lược đồ trong cùng UC                    | CartApplicationService, CartActionResult nhất quán          | Kiểm tra tên trong mục 5–6                         |
| Truy vết                 | Đối chiếu bảng tham chiếu (mục 7) với lược đồ tuần tự nội bộ (mục 6.5) | Mọi tương tác trong sequence đều có entry trong bảng        | Kiểm tra không thiếu endpoint/method               |
| Xóa thành công           | removeBook() khi sách có trong giỏ                                     | CartActionResult.success(), giỏ không còn CartItem đó       | Test: removeBookDeletesItemFromCart                |
| No-op khi sách không có  | removeBook() với bookId không có trong giỏ                             | CartActionResult.success(), giỏ giữ nguyên                  | Test: removeMissingBookBehavesAsNoOp               |
| Parse bookId lỗi         | POST với bookId = "abc"                                                | FieldValidationException("bookId", "Ma sach khong hop le.") | Code: CartPageBean.parseLong()                     |
| Không gọi BookRepository | removeBook() không truy vấn bảng books                                 | Không có SELECT trên bảng books trong luồng remove          | Kiểm tra code: CartApplicationService.removeBook() |
| Hành vi idempotent       | Gọi removeBook() 2 lần liên tiếp cùng bookId                           | Lần 1: xóa CartItem. Lần 2: no-op, vẫn success              | Đảm bảo idempotent behavior                        |
