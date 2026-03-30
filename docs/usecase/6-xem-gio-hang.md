# 6. Xem giỏ hàng

## Mô tả

Khách hàng truy cập trang giỏ hàng để xem toàn bộ các sách đã thêm vào. Hệ thống truy vấn giỏ hàng của khách hàng từ cơ sở dữ liệu, kết hợp thông tin sách chi tiết từ bảng sách để hiển thị đầy đủ thông tin mỗi dòng: tên sách, giá tại thời điểm thêm vào, số lượng, và tổng tiền từng dòng. Nếu giỏ trống, trang sẽ hiển thị trạng thái rỗng kèm đường dẫn quay lại danh mục.

## Bảng mô tả use case

| Thuộc tính        | Nội dung                                                                    |
|-------------------|-----------------------------------------------------------------------------|
| Mã                | UC-06                                                                       |
| Tên               | Xem giỏ hàng                                                                |
| Tác nhân         | Khách hàng (Customer)                                                       |
| Mô tả            | Khách hàng xem danh sách các sách đã thêm vào giỏ cùng thông tin chi tiết   |
| Điều kiện tiên   | Khách hàng đã đăng nhập, đang ở trang giỏ hàng                              |
| Kết quả           | Trang hiển thị danh sách sách trong giỏ hoặc trạng thái giỏ trống          |

## Sequence Diagram

```plantuml
@startuml
skinparam backgroundColor #FEFEFE

actor "Khách hàng" as Customer
participant "CartPageBean" as Page #skyblue
participant "CartApplicationService" as CartService #lightgreen
participant "CartViewAssembler" as Assembler #lightgreen
participant "CartRepository" as CartRepo #lightyellow
participant "BookRepository" as BookRepo #lightyellow

Customer -> Page: Truy cập GET /account/cart.jsp\n(AuthGuard đã xác minh session)
activate Page

Page -> CartService: getCart(customerId)
activate CartService

CartService -> CartRepo: findByCustomerId(customerId)
activate CartRepo
CartRepo --> CartService: Optional<Cart>
deactivate CartRepo

alt Không có giỏ hàng
    CartService -> CartService: emptyCart(customerId)
    note right
        Tạo Cart rỗng tạm thời:
        - id = null
        - items = List.of()
        - createdAt = now
    end
end

CartService -> Assembler: toCartView(cart, bookRepository)
activate Assembler
activate BookRepo

loop Mỗi CartItem
    Assembler -> BookRepo: findById(cartItem.bookId)
    BookRepo --> Assembler: Book
    Assembler -> Assembler: CartLineView
    note right
        Tạo CartLineView cho mỗi dòng:
        - bookId, title, author
        - unitPrice (từ unit_price_snapshot)
        - quantity, lineTotal
        - availableStock
    end
end

Assembler --> CartService: CartView
deactivate Assembler
deactivate BookRepo

CartService --> Page: CartView
deactivate CartService

alt CartView có items
    Page -> Customer: Render /account/cart.jsp\nvới danh sách CartLineView\n+ Form cập nhật / xóa từng dòng
else CartView trống
    Page -> Customer: Render trạng thái giỏ trống\n+ Link quay lại /account/index.jsp
end

deactivate Page

@enduml
```
<!-- docs/images/usecase/uc-06.svg -->
![skinparam](docs/images/usecase/uc-06.svg)





## Exception Flows

| Exception                              | Thông báo cho người dùng              | Hành vi hệ thống              |
|----------------------------------------|---------------------------------------|-------------------------------|
| Không có sách nào trong giỏ           | Trang trống + link quay lại danh mục | Hiển thị empty state         |
| Lỗi khi truy vấn cơ sở dữ liệu       | "Đã xảy ra lỗi khi tải giỏ hàng."   | Hiển thị thông báo lỗi chung |

## Chi tiết hiển thị giỏ hàng

Mỗi dòng trong giỏ hàng hiển thị thông tin được **snapshot tại thời điểm thêm vào**:

| Trường          | Nguồn dữ liệu                          |
|-----------------|----------------------------------------|
| Tên sách        | `unit_price_snapshot` (giá đã lưu)     |
| Đơn giá         | `CartItem.unit_price_snapshot`         |
| Số lượng        | `CartItem.quantity`                    |
| Tổng dòng       | `unitPrice × quantity`                 |
| Tồn kho khả dụng | `Book.stockQuantity` (check thời gian thực) |