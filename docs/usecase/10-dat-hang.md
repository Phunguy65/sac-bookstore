# 10. Đặt hàng

## Mô tả

Khách hàng xác nhận đặt hàng từ trang thanh toán. Hệ thống giải quyết địa chỉ giao hàng (dùng địa chỉ mặc định hoặc tạo mới từ form), kiểm tra lần cuối tất cả sách trong giỏ còn đủ tồn kho và còn được bán, trừ tồn kho từng cuốn sách, tạo đơn hàng với đầy đủ thông tin snapshot, xóa giỏ hàng và chuyển hướng đến trang chi tiết đơn hàng vừa tạo.

## Bảng mô tả use case

| Thuộc tính        | Nội dung                                                                    |
|-------------------|-----------------------------------------------------------------------------|
| Mã                | UC-10                                                                       |
| Tên               | Đặt hàng                                                                    |
| Tác nhân         | Khách hàng (Customer)                                                       |
| Mô tả            | Khách hàng xác nhận đặt hàng từ giỏ hàng, hệ thống tạo đơn và trừ tồn kho |
| Điều kiện tiên   | Khách hàng đã đăng nhập, có ít nhất một sách trong giỏ                    |
| Kết quả           | Đơn hàng được tạo, giỏ hàng bị xóa, khách hàng chuyển đến trang chi tiết đơn |

## Sequence Diagram

```plantuml
@startuml
skinparam backgroundColor #FEFEFE

actor "Khách hàng" as Customer
participant "CheckoutPageBean" as Page #skyblue
participant "CheckoutApplicationService" as CheckoutService #lightgreen
participant "CartRepository" as CartRepo #lightyellow
participant "BookRepository" as BookRepo #lightyellow
participant "AddressRepository" as AddressRepo #lightyellow
participant "OrderRepository" as OrderRepo #lightyellow

Customer -> Page: POST /account/checkout.jsp\n(Địa chỉ giao hàng)
activate Page

Page -> CheckoutService: placeOrder(customerId, addressInput)
activate CheckoutService

CheckoutService -> CartRepo: findByCustomerId(customerId)
activate CartRepo
CartRepo --> CheckoutService: Optional<Cart>
deactivate CartRepo

alt Giỏ hàng trống hoặc không tồn tại
    CheckoutService --> Page: CheckoutResult.failure("Gio hang dang trong.")
else Giỏ hàng có hàng
    CheckoutService -> CheckoutService: resolveShippingAddress(customerId, addressInput)
    activate CheckoutService

    alt Có địa chỉ mặc định
        CheckoutService -> CheckoutService: Dùng địa chỉ hiện có\nkhông tạo mới
    else Không có địa chỉ mặc định
        alt addressInput = null
            CheckoutService --> Page: CheckoutResult.failure("Dia chi giao hang la bat buoc.")
        else
            CheckoutService -> CheckoutService: Tạo AddressDetails mới\ntừ form input
            CheckoutService -> CheckoutService: Tạo Address mới isDefault=true\nđể lưu vào database
        end
    end

    CheckoutService -> CheckoutService: prepareOrder(cart, shippingAddress)
    note right
        Với mỗi CartItem:
        1. Tìm Book tương ứng
        2. Kiểm tra active = true
        3. Kiểm tra stock >= quantity
        4. Tính lineTotal = price × quantity
        5. Tạo OrderItem với snapshot:
           - bookId, bookTitleSnapshot, bookIsbnSnapshot
           - unitPriceSnapshot, quantity, lineTotal
        6. Tạo BookStockChange (trừ tồn kho)
        7. Cộng dồn totalAmount
    end note

    loop Mỗi BookStockChange
        CheckoutService -> BookRepo: save(updatedBook)
        activate BookRepo
        BookRepo --> CheckoutService: Book (đã cập nhật tồn kho)
        deactivate BookRepo
    end

    CheckoutService -> OrderRepo: save(Order)
    activate OrderRepo
    OrderRepo --> CheckoutService: Order (đã lưu)
    deactivate OrderRepo

    alt Tạo địa chỉ mới
        CheckoutService -> AddressRepo: save(newDefaultAddress)
        activate AddressRepo
        AddressRepo --> CheckoutService: Address (đã lưu)
        deactivate AddressRepo
    end

    CheckoutService -> CartRepo: save(emptyCart)
    activate CartRepo
    note right
        Giỏ hàng được XÓA SẠCH sau khi đặt hàng thành công.
        Tất cả CartItem bị loại bỏ.
    end
    CartRepo --> CheckoutService: Cart (đã xóa items)
    deactivate CartRepo

    CheckoutService --> Page: CheckoutResult.success(orderId)
    deactivate CheckoutService

    alt Đặt hàng thành công
        Page -> Customer: Redirect → /account/order-detail.jsp?orderId={id}
    else Đặt hàng thất bại (lỗi validate)
        Page -> Customer: Hiển thị thông báo lỗi\n+ Giữ form địa chỉ đã nhập\n+ Giữ ở /account/checkout.jsp
    end
end

deactivate Page

@enduml
```
<!-- docs/images/usecase/uc-10.svg -->
![skinparam](docs/images/usecase/uc-10.svg)





## Exception Flows

| Exception                                | Thông báo cho người dùng                                | Hành vi hệ thống                            |
|------------------------------------------|---------------------------------------------------------|---------------------------------------------|
| Giỏ hàng trống                         | "Gio hang dang trong."                                | Redirect về trang giỏ hàng                |
| Không có địa chỉ mặc định và form trống | "Dia chi giao hang la bat buoc."                   | Hiển thị lỗi tại các trường địa chỉ      |
| Sách trong giỏ không còn được bán      | "Co sach trong gio hang hien khong con mo ban."    | Hiển thị lỗi ở đầu trang                 |
| Tồn kho không đủ                       | "Khong du ton kho de hoan tat don hang."         | Hiển thị lỗi ở đầu trang                 |
| Sách trong giỏ không tồn tại           | "Khong tim thay sach trong gio hang."           | Hiển thị lỗi ở đầu trang                 |

## Chi tiết snapshot pattern

Khi tạo đơn hàng, hệ thống **không lưu liên kết** đến `BookEntity` mà **snapshot toàn bộ thông tin** tại thời điểm đặt hàng:

```
OrderItem lưu:
├─ bookId              ← ID gốc của sách
├─ bookTitleSnapshot   ← Tên sách tại thời điểm đặt
├─ bookIsbnSnapshot    ← ISBN tại thời điểm đặt
├─ unitPriceSnapshot   ← Đơn giá tại thời điểm đặt
├─ quantity            ← Số lượng đặt
└─ lineTotal           ← Tổng = price × quantity
```

Điều này đảm bảo đơn hàng không bị ảnh hưởng nếu sách được chỉnh sửa hoặc ngừng bán sau khi đặt.

## Chi tiết địa chỉ giao hàng — hai trường hợp

```
TRƯỜNG HỢP 1: Đã có địa chỉ mặc định
├─ Dùng trực tiếp AddressDetails từ DB
├─ KHÔNG tạo bản ghi Address mới
└─ shippingAddress được gán từ địa chỉ hiện có

TRƯỜNG HỢP 2: Không có địa chỉ mặc định
├─ Validate form địa chỉ đầy đủ
├─ Tạo Address mới, isDefault = true
├─ Lưu vào database
└─ shippingAddress được gán từ form
```

## Chi tiết tồn kho

Hệ thống kiểm tra tồn kho **hai lần**:
1. Lúc thêm sách vào giỏ (CartApplicationService)
2. Lúc đặt hàng (CheckoutApplicationService) — phòng trường hợp tồn kho thay đổi trong khoảng thời gian giữa lúc thêm và lúc đặt.