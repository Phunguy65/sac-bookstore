# 5. Thêm sách vào giỏ

## Mô tả

Khách hàng đang xem danh mục sách chọn một cuốn sách cùng số lượng và nhấn nút thêm vào giỏ. Hệ thống kiểm tra sách tồn tại, đang được bán, và đủ tồn kho cho số lượng yêu cầu. Nếu sách đã có trong giỏ, số lượng sẽ được cộng dồn. Kết quả được phản hồi ngay trên trang danh mục mà không cần chuyển trang.

## Bảng mô tả use case

| Thuộc tính        | Nội dung                                                                    |
|-------------------|-----------------------------------------------------------------------------|
| Mã                | UC-05                                                                       |
| Tên               | Thêm sách vào giỏ                                                           |
| Tác nhân         | Khách hàng (Customer)                                                       |
| Mô tả            | Khách hàng thêm một cuốn sách với số lượng chỉ định vào giỏ hàng cá nhân    |
| Điều kiện tiên   | Khách hàng đã đăng nhập, đang ở trang danh mục sách                        |
| Kết quả           | Sách được thêm vào giỏ hàng (hoặc cộng dồn số lượng), thông báo thành công  |

## Sequence Diagram

```plantuml
@startuml
skinparam backgroundColor #FEFEFE

actor "Khách hàng" as Customer
participant "AccountHomePageBean" as Page #skyblue
participant "AccountCatalogApplicationService" as CatalogService #lightgreen
participant "CartApplicationService" as CartService #lightgreen
participant "BookRepository" as BookRepo #lightyellow
participant "CartRepository" as CartRepo #lightyellow

Customer -> Page: POST /account/index.jsp\n(bookId, quantity)
activate Page

Page -> CatalogService: addBook(customerId, bookId, quantity)
activate CatalogService

CatalogService -> CartService: addBook(customerId, bookId, quantity)
activate CartService

alt Số lượng <= 0
    CartService --> CatalogService: CartActionResult.failure("So luong khong hop le.")
else Số lượng > 0
    CartService -> BookRepo: findById(bookId)
    activate BookRepo

    alt Sách không tồn tại
        BookRepo --> CartService: Optional.empty()
        CartService --> CatalogService: CartActionResult.failure("Khong tim thay sach duoc yeu cau.")
    else Sách tồn tại
        BookRepo --> CartService: Book
        deactivate BookRepo

        alt Sách không còn được bán (active = false)
            CartService --> CatalogService: CartActionResult.failure("Sach nay hien khong con mo ban.")
        else Sách đã hết hàng (stock = 0)
            CartService --> CatalogService: CartActionResult.failure("Sach nay da het hang.")
        else Số lượng vượt tồn kho
            CartService --> CatalogService: CartActionResult.failure("So luong vuot qua ton kho hien tai.")
        else Hợp lệ
            CartService -> CartRepo: findByCustomerId(customerId)
            activate CartRepo
            CartRepo --> CartService: Optional<Cart>
            deactivate CartRepo

            alt Tìm thấy giỏ
                loop Tìm sách trong giỏ
                    CartService -> CartService: indexOfBook(items, bookId)
                end

                alt Sách đã có trong giỏ
                    note right
                        Cộng dồn: nextQuantity =
                        existingQuantity + requestedQuantity
                    end
                    CartService -> CartService: ensureWithinStock(book, nextQuantity)
                else Sách chưa có trong giỏ
                    CartService -> CartService: ensureWithinStock(book, quantity)
                end

                CartService -> CartRepo: save(updatedCart)
                activate CartRepo
                CartRepo --> CartService: Cart (đã lưu)
                deactivate CartRepo

                CartService --> CatalogService: CartActionResult.success()
            end
        end
    end
end

deactivate CartService
deactivate CatalogService

alt Thêm thành công
    Page -> Customer: Hiển thị "Da them sach vao gio hang."\n+ Reset ô số lượng về 1
else Thêm thất bại (lỗi trường quantity)
    Page -> Customer: Hiển thị lỗi ngay tại dòng sách đó\n(trường quantity)
else Thêm thất bại (lỗi khác)
    Page -> Customer: Hiển thị thông báo lỗi chung\nở đầu trang
end

deactivate Page

@enduml
```
<!-- docs/images/usecase/uc-05.svg -->
![skinparam](docs/images/usecase/uc-05.svg)





## Exception Flows

| Exception                                | Thông báo cho người dùng                     | Hành vi hệ thống                     |
|------------------------------------------|----------------------------------------------|--------------------------------------|
| Số lượng không hợp lệ (<= 0 hoặc không phải số) | "So luong khong hop le."               | Hiển thị lỗi tại trường quantity của dòng sách |
| Sách không tồn tại                       | "Khong tim thay sach duoc yeu cau."         | Hiển thị lỗi ở đầu trang            |
| Sách không còn được bán                  | "Sach nay hien khong con mo ban."           | Hiển thị lỗi ở đầu trang            |
| Sách đã hết hàng                         | "Sach nay da het hang."                    | Hiển thị lỗi ở đầu trang            |
| Số lượng vượt tồn kho hiện tại          | "So luong vuot qua ton kho hien tai."      | Hiển thị lỗi tại trường quantity của dòng sách |

## Chi tiết hành vi cộng dồn

Khi một cuốn sách đã tồn tại trong giỏ hàng, hệ thống sẽ **cộng dồn số lượng** thay vì tạo dòng mới:

```
Giỏ hiện tại: Sách A × 2
Người dùng thêm: Sách A × 3
─────────────────────────
Kết quả:        Sách A × 5
```

Sau đó hệ thống kiểm tra lại tồn kho để đảm bảo tổng số lượng không vượt quá số lượng trong kho.