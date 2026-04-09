# UC-04: Cập nhật số lượng

## 1. Mô tả use case

| Mục                            | Nội dung                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
| ------------------------------ | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Phụ thuộc                      | UC-03 (Xem giỏ hàng) — khách hàng phải đang ở trang giỏ hàng để thực hiện cập nhật số lượng.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
| Mục đích                       | Khách hàng muốn thay đổi số lượng một cuốn sách đã có trong giỏ. PM giúp kiểm tra tính hợp lệ (sách tồn tại, đang bán, đủ tồn kho), cập nhật số lượng mới cùng giá hiện tại, và lưu giỏ hàng.                                                                                                                                                                                                                                                                                                                                                                                                                                                   |
| Mô tả                          | Khách hàng thay đổi số lượng của một cuốn sách trong giỏ hàng. Hệ thống kiểm tra sách vẫn còn được bán, số lượng hợp lệ (> 0) và không vượt tồn kho hiện tại, sau đó cập nhật CartItem với số lượng mới và giá sách hiện tại.                                                                                                                                                                                                                                                                                                                                                                                                                   |
| Actor chính                    | Khách hàng (Customer)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
| Actor liên quan                | Không                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
| Tiền điều kiện                 | Khách hàng đã truy cập vào hệ thống (có session hợp lệ), đang ở trang giỏ hàng, giỏ có ít nhất một sách.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
| Dãy lệnh thực hiện bình thường | 1. Khách hàng nhập số lượng mới và nhấn nút cập nhật cho một cuốn sách (POST /account/cart.jsp với action=update, bookId, quantity). <br> 2. Hệ thống kiểm tra quantity > 0. <br> 3. Hệ thống tìm sách theo bookId, kiểm tra active = true và stockQuantity > 0. <br> 4. Hệ thống kiểm tra quantity không vượt tồn kho. <br> 5. Hệ thống tìm giỏ hàng của khách hàng, tìm CartItem theo bookId. <br> 6. Hệ thống cập nhật CartItem với quantity mới và unitPriceSnapshot = giá sách hiện tại (refresh giá). <br> 7. Hệ thống lưu giỏ hàng cập nhật. <br> 8. Hệ thống hiển thị thông báo "Gio hang da duoc cap nhat." và refresh trang giỏ hàng. |
| Hậu điều kiện (thành công)     | CartItem được cập nhật với số lượng mới và giá sách hiện tại (unitPriceSnapshot refresh). Giỏ hàng đã lưu trong DB.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |
| Hậu điều kiện (thất bại)       | Giỏ hàng không thay đổi. Không có CartItem nào được cập nhật. Trang hiển thị lỗi tương ứng (inline tại trường quantity hoặc lỗi chung đầu trang).                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
| Xử lý ngoại lệ                 | quantity <= 0 hoặc không phải số → "So luong khong hop le." (lỗi inline tại trường quantity) <br> bookId không phải số → "Ma sach khong hop le." (lỗi đầu trang) <br> Sách không tồn tại → "Khong tim thay sach duoc yeu cau." (lỗi đầu trang) <br> Sách không còn bán (active = false) → "Sach nay hien khong con mo ban." (lỗi đầu trang) <br> Sách hết hàng (stock = 0) → "Sach nay da het hang." (lỗi đầu trang) <br> Số lượng vượt tồn kho → "So luong vuot qua ton kho hien tai." (lỗi inline tại trường quantity) <br> Sách không có trong giỏ hàng → "Sach khong ton tai trong gio hang." (lỗi đầu trang)                               |

## 2. Lược đồ tuần tự

<!-- Lược đồ cấp 1: Actor ↔ PM (hệ thống là hộp đen). -->

```plantuml
@startuml UC-04
title UC-04: Cập nhật số lượng

actor "Khách hàng" as Actor
participant "Hệ thống" as API

Actor -> API: POST /account/cart.jsp(action=update, bookId, quantity)
alt bookId không phải số
    API --> Actor: 200 + Error("Ma sach khong hop le.")
else quantity <= 0 hoặc không phải số
    API --> Actor: 200 + FieldError("quantity", "So luong khong hop le.")
else Sách không tồn tại
    API --> Actor: 200 + Error("Khong tim thay sach duoc yeu cau.")
else Sách không còn bán (active = false)
    API --> Actor: 200 + Error("Sach nay hien khong con mo ban.")
else Sách hết hàng (stock = 0)
    API --> Actor: 200 + Error("Sach nay da het hang.")
else Số lượng vượt tồn kho
    API --> Actor: 200 + FieldError("quantity", "So luong vuot qua ton kho hien tai.")
else Sách không có trong giỏ
    API --> Actor: 200 + Error("Sach khong ton tai trong gio hang.")
else Thành công
    API -> API: validate, tìm sách, tìm giỏ, cập nhật CartItem (qty + giá mới), lưu giỏ
    API --> Actor: 200 + Success("Gio hang da duoc cap nhat.")
end
@enduml
```

## 3. Lược đồ hoạt động

```plantuml
@startuml UC-04-activity
title UC-04: Cập nhật số lượng - Activity Diagram

start

:Khách hàng POST /account/cart.jsp
(action=update, bookId, quantity);

if (bookId là số hợp lệ?) then (không)
  :Trả Error đầu trang
  "Ma sach khong hop le.";
  stop
else (có)
endif

if (quantity > 0 và là số hợp lệ?) then (không)
  :Trả FieldError tại trường quantity
  "So luong khong hop le.";
  stop
else (có)
endif

:Tìm sách theo bookId;

if (Sách tồn tại?) then (không)
  :Trả Error đầu trang
  "Khong tim thay sach duoc yeu cau.";
  stop
else (có)
endif

if (Sách đang bán (active = true)?) then (không)
  :Trả Error đầu trang
  "Sach nay hien khong con mo ban.";
  stop
else (có)
endif

if (Sách còn hàng (stock > 0)?) then (không)
  :Trả Error đầu trang
  "Sach nay da het hang.";
  stop
else (có)
endif

if (quantity <= stockQuantity?) then (không)
  :Trả FieldError tại trường quantity
  "So luong vuot qua ton kho hien tai.";
  stop
else (có)
endif

:Tìm giỏ hàng của khách hàng
(hoặc tạo giỏ rỗng nếu chưa có);

:Tìm CartItem theo bookId (indexOfBook);

if (CartItem tồn tại?) then (không)
  :Trả Error đầu trang
  "Sach khong ton tai trong gio hang.";
  stop
else (có)
endif

:Cập nhật CartItem:
- quantity = số lượng mới
- unitPriceSnapshot = giá sách hiện tại
  (book.price() — refresh giá);

:Lưu giỏ hàng cập nhật;

:Hiển thị "Gio hang da duoc cap nhat."
+ Refresh trang giỏ hàng;

stop
@enduml
```

## 4. Lược đồ trạng thái

```plantuml
@startuml UC-04-state
title UC-04: Cập nhật số lượng - Cart State Diagram

[*] --> CoHang : Giỏ đã có ít nhất 1 CartItem

CoHang --> CoHang : updateQuantity() thành công\n(CartItem.quantity thay đổi\n+ unitPriceSnapshot refresh)

state CoHang {
  state "CartItem trước update" as Before
  state "CartItem sau update" as After
  Before --> After : quantity = newQty\nunitPriceSnapshot = book.price()
}

note right of CoHang
  CHECK constraint DB:
  - cart_items.quantity > 0
  - cart_items.unit_price_snapshot >= 0
  - UNIQUE(cart_id, book_id)

  Business rule:
  - quantity > 0 (Require.positive)
  - quantity <= book.stockQuantity
  - book.active = true
  - book.stockQuantity > 0
end note
@enduml
```

## 5. Lược đồ lớp ý niệm

```plantuml
@startuml UC-04-class
title UC-04: Cập nhật số lượng - Conceptual Class Diagram

class "Book" as BookEntity {
  - id: BookId
  - isbn: Isbn
  - title: BookTitle
  - author: AuthorName
  - price: Money
  - stockQuantity: Quantity
  - active: boolean
}

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
  + failure(msg, fieldErrors): CartActionResult
}

class "BookId" as BookIdVO {
  - value: Long
}

class "CustomerId" as CustIdVO {
  - value: Long
}

class "Money" as MoneyVO {
  - amount: BigDecimal
}

class "Quantity" as QtyVO {
  - value: int
}

CartEntity "1" *-- "0..*" CartItemEntity : items
CartItemEntity --> BookIdVO : bookId
CartEntity --> CustIdVO : customerId
CartItemEntity *-- MoneyVO : unitPriceSnapshot
CartItemEntity *-- QtyVO : quantity
@enduml
```

## 6. Phân rã thành phần PM

### 6.1 Controller: `CartPageBean`

- **Nhiệm vụ**: Nhận HTTP POST request từ khách hàng (action=update, bookId,
  quantity), parse tham số, ủy thác cho UseCase, xử lý kết quả (thành công/lỗi
  inline/lỗi chung), sau đó gọi getCart() để refresh trang.
- **Endpoint**: `POST /account/cart.jsp`
- **Input**: `CartPageRequest` —
  `{ method: "POST", action: "update", bookId: String, quantity: String, infoParam: null }`
- **Output thành công**: `200` + `CartPageResult(RENDER, CartPageModel)` — model
  chứa CartView mới + infoMessage "Gio hang da duoc cap nhat."
- **Output lỗi**: `200` + `CartPageResult(RENDER, CartPageModel)` — model chứa
  CartView + errorMessage hoặc lineQuantityError tại dòng sách (lineErrorBookId
  = submittedBookId).

### 6.2 UseCase: `CartApplicationService`

- **Nhiệm vụ**: Orchestrate nghiệp vụ cập nhật số lượng sách trong giỏ.
- **Input**: `CustomerId`, `bookIdValue: long`, `quantityValue: int`
- **Output**: `CartActionResult`
- **Gọi đến**:
    - `Require.positive(quantityValue)` — validate quantity > 0
    - `BookRepository.findById(bookId)` — tìm sách, kiểm tra tồn tại + active +
      stock > 0
    - `ensureWithinStock(book, quantity)` — kiểm tra quantity không vượt tồn kho
    - `CartRepository.findByCustomerId(customerId)` — tìm giỏ hàng (hoặc tạo giỏ

        rỗng)

    - `indexOfBook(items, bookId)` — tìm sách trong giỏ
    - `CartRepository.save(updatedCart)` — lưu giỏ hàng cập nhật

- **Phát sinh sự kiện**: Không.

### 6.3 Repository: `BookRepository` + `CartRepository`

**BookRepository** (impl: `BookJpaRepository`):

- **Nhiệm vụ**: Truy xuất domain entity `Book`.
- **Phương thức liên quan đến UC**:
    - `findById(BookId): Optional<Book>` — tìm sách theo ID để kiểm tra tồn tại,
      active, stock.
- **Table**: `books`

**CartRepository** (impl: `CartJpaRepository`):

- **Nhiệm vụ**: Truy xuất/lưu trữ domain entity `Cart` kèm `CartItem`.
- **Phương thức liên quan đến UC**:
    - `findByCustomerId(CustomerId): Optional<Cart>` — tìm giỏ hàng của khách
      hàng (LEFT JOIN FETCH items).
    - `save(Cart): Cart` — lưu giỏ hàng (persist nếu mới, merge nếu đã tồn tại).
- **Tables**: `carts`, `cart_items`

### 6.5 Lược đồ tuần tự nội bộ PM

```plantuml
@startuml UC-04-internal
title UC-04: Cập nhật số lượng - Internal Sequence

actor "Khách hàng" as Actor
participant "CartPageBean" as CTL
participant "CartApplicationService" as UC
participant "BookJpaRepository" as BREPO
participant "CartJpaRepository" as CREPO
database "DB" as DB

Actor -> CTL: POST /account/cart.jsp\n(action=update, bookId, quantity)
CTL -> CTL: parseLong(bookId), parseInt(quantity)

alt parseLong/parseInt thất bại
    CTL --> Actor: 200 + Render trang\n+ Lỗi parse
else parse thành công
    CTL -> UC: updateQuantity(customerId, bookId, quantity)

    UC -> UC: Require.positive(quantity)
    alt quantity <= 0
        UC --> CTL: CartActionResult.failure("quantity must be positive")
    else quantity > 0
        UC -> BREPO: findById(BookId(bookId))
        BREPO -> DB: SELECT FROM books WHERE id = ?
        DB --> BREPO: BookEntity | null
        BREPO --> UC: Optional<Book>

        alt Sách không tồn tại hoặc inactive hoặc hết hàng
            UC --> CTL: CartActionResult.failure(error message)
        else Hợp lệ
            UC -> UC: ensureWithinStock(book, quantity)

            alt Vượt tồn kho
                UC --> CTL: CartActionResult.failure("So luong vuot qua ton kho hien tai.")
            else Trong tồn kho
                UC -> CREPO: findByCustomerId(customerId)
                CREPO -> DB: SELECT FROM carts LEFT JOIN cart_items\nWHERE customer_id = ?
                DB --> CREPO: CartEntity | null
                CREPO --> UC: Optional<Cart>

                UC -> UC: indexOfBook(items, bookId)

                alt Sách không có trong giỏ (index < 0)
                    UC --> CTL: CartActionResult.failure("Sach khong ton tai trong gio hang.")
                else Sách có trong giỏ
                    UC -> UC: new CartItem(existingItem.id(),\nexistingItem.bookId(), quantity,\nbook.price(), existingItem.version(),\nexistingItem.createdAt(), Instant.now())
                    UC -> CREPO: save(updatedCart)
                    CREPO -> DB: UPDATE carts + cart_items
                    DB --> CREPO: CartEntity
                    CREPO --> UC: Cart
                    UC --> CTL: CartActionResult.success()
                end
            end
        end
    end

    CTL -> UC: getCart(customerId)
    UC --> CTL: CartView

    alt Thành công
        CTL --> Actor: 200 + Render trang\n"Gio hang da duoc cap nhat." + CartView
    else Lỗi field (quantity)
        CTL --> Actor: 200 + Render trang\n+ Lỗi inline tại dòng sách + CartView
    else Lỗi chung
        CTL --> Actor: 200 + Render trang\n+ Lỗi đầu trang + CartView
    end
end
@enduml
```

## 7. Bảng tham chiếu dò vết

| Use Case | Controller   | Endpoint                                 | UseCase                                 | Repository                           | Table             |
| -------- | ------------ | ---------------------------------------- | --------------------------------------- | ------------------------------------ | ----------------- |
| UC-04    | CartPageBean | `POST /account/cart.jsp` (action=update) | CartApplicationService.updateQuantity() | BookJpaRepository.findById()         | books             |
|          |              |                                          |                                         | CartJpaRepository.findByCustomerId() | carts, cart_items |
|          |              |                                          |                                         | CartJpaRepository.save()             | carts, cart_items |
|          |              |                                          | CartApplicationService.getCart()        | CartJpaRepository.findByCustomerId() | carts, cart_items |
|          |              |                                          | CartViewAssembler.toCartView()          | BookJpaRepository.findByIds()        | books             |

## 8. Tiêu chí kiểm thử

| Tiêu chí                     | Phép thử                                                               | Kết quả mong đợi                                                   | Ghi chú                                             |
| ---------------------------- | ---------------------------------------------------------------------- | ------------------------------------------------------------------ | --------------------------------------------------- |
| Toàn diện (coverage)         | Đối chiếu Activity ↔ Sequence: mọi luồng đều được thể hiện             | Không bỏ sót luồng chính lẫn 7 ngoại lệ                            | Rà soát chéo mục 2 và mục 3                         |
| Nhất quán                    | Rà soát tên lớp, API giữa các lược đồ trong cùng UC                    | CartApplicationService, CartActionResult, BookRepository nhất quán | Kiểm tra tên trong mục 5–6                          |
| Truy vết                     | Đối chiếu bảng tham chiếu (mục 7) với lược đồ tuần tự nội bộ (mục 6.5) | Mọi tương tác trong sequence đều có entry trong bảng               | Kiểm tra không thiếu endpoint/method                |
| Cập nhật thành công          | updateQuantity() khi sách hợp lệ, trong giỏ, qty hợp lệ                | CartActionResult.success(), quantity = newQty                      | Test: updateQuantityPersistsNewQuantity             |
| Giá được refresh             | updateQuantity() kiểm tra unitPriceSnapshot sau cập nhật               | unitPriceSnapshot = book.price() hiện tại, không phải snapshot cũ  | Code: CartApplicationService.java:99                |
| Từ chối vượt tồn kho         | updateQuantity() với quantity > stock                                  | CartActionResult.failure, giỏ không thay đổi                       | Test: rejectsUpdateQuantityThatExceedsStock         |
| Từ chối sách hết hàng        | updateQuantity() với sách có stock = 0                                 | CartActionResult.failure("Sach nay da het hang.")                  | Test: rejectsUpdateQuantityOnZeroStockBook          |
| Từ chối quantity <= 0        | updateQuantity() với quantity = -5                                     | CartActionResult.failure("quantity must be positive")              | Test: rejectsNegativeQuantityWhenUpdatingBook       |
| Từ chối sách không trong giỏ | updateQuantity() với bookId không có trong giỏ                         | CartActionResult.failure("Sach khong ton tai trong gio hang.")     | Code: CartApplicationService.java:91                |
| Parse bookId lỗi             | POST với bookId = "abc"                                                | FieldValidationException("bookId", "Ma sach khong hop le.")        | Test: cartPageShowsInlineErrorForInvalidUpdateInput |
| Parse quantity lỗi           | POST với quantity = "abc"                                              | FieldValidationException("quantity", "So luong khong hop le.")     | Test: cartPageShowsInlineErrorForInvalidUpdateInput |
