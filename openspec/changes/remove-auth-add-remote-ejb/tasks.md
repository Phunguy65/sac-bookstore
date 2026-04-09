# Tasks

## 1. Shared Utilities and Constants

- [x] 1.1 Move `HtmlEscaper` from `auth.interfaces.web` to `shared.interfaces.web`. Update package declaration. Move `HtmlEscaperTest` to corresponding test package.
- [x] 1.2 Update all 5 account JSP files to import `shared.interfaces.web.HtmlEscaper` instead of `auth.interfaces.web.HtmlEscaper`.
- [x] 1.3 Add `public static final CustomerId DEFAULT_CUSTOMER = new CustomerId(1L)` constant to `CustomerId` record.

## 2. Remove Auth from Purchase Beans

- [x] 2.1 In `AccountHomePageBean`: remove `AuthSession` import, replace `AuthSession.getCustomerId(request)` with `CustomerId.DEFAULT_CUSTOMER`.
- [x] 2.2 In `CartPageBean`: remove `AuthSession` import, replace all 3 occurrences of `AuthSession.getCustomerId(request)` with `CustomerId.DEFAULT_CUSTOMER`.
- [x] 2.3 In `CheckoutPageBean`: remove `AuthSession` import, replace `AuthSession.getCustomerId(request)` with `CustomerId.DEFAULT_CUSTOMER`.
- [x] 2.4 In `OrderHistoryPageBean`: remove `AuthSession` import, replace `AuthSession.getCustomerId(request)` with `CustomerId.DEFAULT_CUSTOMER`.
- [x] 2.5 In `OrderDetailPageBean`: remove `AuthSession` import, replace `AuthSession.getCustomerId(request)` with `CustomerId.DEFAULT_CUSTOMER`.

## 3. Remove Auth JSPs and Guards

- [x] 3.1 Remove `<%@ include file="/WEB-INF/jspf/auth/require-auth.jspf" %>` from all 5 account JSPs (index.jsp, cart.jsp, checkout.jsp, orders.jsp, order-detail.jsp).
- [x] 3.2 In `account/index.jsp`: remove `AuthSession` import, remove logout form/button, remove "Ve trang chu" link to index.jsp.
- [x] 3.3 Delete `src/main/webapp/WEB-INF/jspf/auth/require-auth.jspf`.
- [x] 3.4 Delete `src/main/webapp/auth/login.jsp`.
- [x] 3.5 Delete `src/main/webapp/auth/register.jsp`.
- [x] 3.6 Delete `src/main/webapp/auth/logout.jsp`.
- [x] 3.7 Delete `src/main/webapp/index.jsp`.

## 4. Delete Auth Java Source

- [x] 4.1 Delete auth page beans: `LoginPageBean`, `RegisterPageBean`, `LogoutPageBean`, `AuthGuardBean`.
- [x] 4.2 Delete auth models: `LoginPageModel`, `RegisterPageModel`.
- [x] 4.3 Delete auth utilities: `AuthSession`, `AuthPaths`.
- [x] 4.4 Delete auth application services: `AuthApplicationService`, `LoginResult`, `RegisterResult`, `PasswordHasher`, `BCryptPasswordHasher`.
- [x] 4.5 Delete auth config: `DemoModeSettings` (interface), `DemoModeConfig`.
- [x] 4.6 Delete auth domain (customer only): `Customer`, `CustomerStatus`, `CustomerRepository`.
- [x] 4.7 Delete auth infrastructure (customer only): `CustomerJpaRepository`, `CustomerEntityMapper`. Keep `CustomerEntity` for FK integrity. Keep all Address-related files.
- [x] 4.8 Delete the original `auth.interfaces.web.HtmlEscaper` (already moved in task 1.1).

## 5. Delete Auth Tests

- [x] 5.1 Delete `AuthPageBeansTest`, auth `ServletApiTestSupport`.
- [x] 5.2 Delete `AuthSessionTest`, `LoginResultTest`, `RegisterResultTest`.
- [x] 5.3 Delete `AuthApplicationServiceTest`, `BCryptPasswordHasherTest`.
- [x] 5.4 Delete `DemoModeConfigTest`, `DemoModeConfigurationAlignmentTest`.
- [x] 5.5 Delete `CustomerEntityMapperTest`. Keep `AddressEntityMapperTest`.
- [x] 5.6 Verify build compiles after auth removal: `mvn clean compile -DskipTests`.
- [x] 5.7 Update purchase `ServletApiTestSupport`: remove `AuthSession` import, simplify `authenticated()` method to just set a raw long in session (or remove if unused by remaining tests).
- [x] 5.8 Verify tests pass after auth removal: `mvn test`.

## 6. Add Serializable to All Types

- [x] 6.1 Add `implements Serializable` to shared value object records: `Money`, `BookId`, `Quantity`, `CustomerId`, `OrderId`, `CartId`, `CartItemId`, `OrderItemId`, `AddressId`, `Email`, `FullName`, `PasswordHash`, `PhoneNumber`, `RecipientName`, `AddressLine`, `Ward`, `District`, `City`, `Province`, `PostalCode`, `AddressDetails`.
- [x] 6.2 Add `implements Serializable` to book value object records: `BookTitle`, `Isbn`, `AuthorName`, `BookDescription`, `ImageUrl`.
- [x] 6.3 Add `implements Serializable` to application view records: `CartView`, `CartLineView`, `CatalogBookView`, `OrderSummaryView`, `OrderDetailView`, `OrderAddressView`, `OrderItemDetailView`, `CheckoutAddressInput`.
- [x] 6.4 Add `implements Serializable` to page model classes: `AccountHomePageModel`, `CartPageModel`, `CheckoutPageModel`, `OrderHistoryPageModel`, `OrderDetailPageModel`.
- [x] 6.5 Add `implements Serializable` to `AddressFormData`.

## 7. Create PageAction Enum and Foundation DTOs

- [x] 7.1 Create `PageAction` enum in `purchase.interfaces.web` with values `RENDER`, `REDIRECT`.

## 8. AccountHomePage - Remote EJB Conversion

- [x] 8.1 Create `AccountHomePageRequest` (Serializable): fields `method`, `bookId`, `quantity` (all String).
- [x] 8.2 Create `AccountHomePageResult` (Serializable): fields `action` (PageAction), `model` (AccountHomePageModel).
- [x] 8.3 Create `AccountHomePage` interface with `@Remote`: method `AccountHomePageResult handle(AccountHomePageRequest request)`.
- [x] 8.4 Refactor `AccountHomePageBean`: change to `@Stateless implements AccountHomePage`, add no-arg constructor, change `handle()` to accept `AccountHomePageRequest` and return `AccountHomePageResult`, remove HttpServletRequest/Response parameters.
- [x] 8.5 Refactor `account/index.jsp`: add `jspInit()`/`jspDestroy()` with JNDI lookup for `AccountHomePage`, extract request params into `AccountHomePageRequest`, handle result.

## 9. CartPage - Remote EJB Conversion

- [x] 9.1 Create `CartPageRequest` (Serializable): fields `method`, `action`, `bookId`, `quantity`, `infoParam` (all String).
- [x] 9.2 Create `CartPageResult` (Serializable): fields `action` (PageAction), `model` (CartPageModel).
- [x] 9.3 Create `CartPage` interface with `@Remote`: method `CartPageResult handle(CartPageRequest request)`.
- [x] 9.4 Refactor `CartPageBean`: change to `@Stateless implements CartPage`, add no-arg constructor, change `handle()` to accept `CartPageRequest` and return `CartPageResult`, remove HttpServletRequest/Response.
- [x] 9.5 Refactor `account/cart.jsp`: add `jspInit()`/`jspDestroy()` with JNDI lookup for `CartPage`, extract request params into `CartPageRequest`, handle result.

## 10. CheckoutPage - Remote EJB Conversion

- [x] 10.1 Create `CheckoutPageRequest` (Serializable): fields `method`, `recipientName`, `phoneNumber`, `line1`, `line2`, `ward`, `district`, `city`, `province`, `postalCode` (all String).
- [x] 10.2 Create `CheckoutPageResult` (Serializable): fields `action` (PageAction), `redirectUrl` (String), `model` (CheckoutPageModel).
- [x] 10.3 Create `CheckoutPage` interface with `@Remote`: method `CheckoutPageResult handle(CheckoutPageRequest request)`.
- [x] 10.4 Refactor `CheckoutPageBean`: change to `@Stateless implements CheckoutPage`, add no-arg constructor, change `handle()` to accept `CheckoutPageRequest` and return `CheckoutPageResult`, build redirect URLs internally (cart empty, order success), return REDIRECT action instead of calling response.sendRedirect.
- [x] 10.5 Refactor `account/checkout.jsp`: add `jspInit()`/`jspDestroy()` with JNDI lookup for `CheckoutPage`, extract all form fields into `CheckoutPageRequest`, handle REDIRECT/RENDER actions.

## 11. OrderHistoryPage - Remote EJB Conversion

- [x] 11.1 Create `OrderHistoryPageRequest` (Serializable): fields `pageParam`, `errorParam` (all String).
- [x] 11.2 Create `OrderHistoryPageResult` (Serializable): fields `action` (PageAction), `model` (OrderHistoryPageModel).
- [x] 11.3 Create `OrderHistoryPage` interface with `@Remote`: method `OrderHistoryPageResult handle(OrderHistoryPageRequest request)`.
- [x] 11.4 Refactor `OrderHistoryPageBean`: change to `@Stateless implements OrderHistoryPage`, add no-arg constructor, change `handle()` to accept `OrderHistoryPageRequest` and return `OrderHistoryPageResult`, remove HttpServletRequest/Response.
- [x] 11.5 Refactor `account/orders.jsp`: add `jspInit()`/`jspDestroy()` with JNDI lookup for `OrderHistoryPage`, extract request params into `OrderHistoryPageRequest`, handle result.

## 12. OrderDetailPage - Remote EJB Conversion

- [x] 12.1 Create `OrderDetailPageRequest` (Serializable): fields `orderIdParam` (String).
- [x] 12.2 Create `OrderDetailPageResult` (Serializable): fields `action` (PageAction), `redirectUrl` (String), `model` (OrderDetailPageModel).
- [x] 12.3 Create `OrderDetailPage` interface with `@Remote`: method `OrderDetailPageResult handle(OrderDetailPageRequest request)`.
- [x] 12.4 Refactor `OrderDetailPageBean`: change to `@Stateless implements OrderDetailPage`, add no-arg constructor, change `handle()` to accept `OrderDetailPageRequest` and return `OrderDetailPageResult`, return REDIRECT action for invalid/missing orders instead of calling response.sendRedirect.
- [x] 12.5 Refactor `account/order-detail.jsp`: add `jspInit()`/`jspDestroy()` with JNDI lookup for `OrderDetailPage`, extract request params into `OrderDetailPageRequest`, handle REDIRECT/RENDER actions.

## 13. Update Tests

- [x] 13.1 Update `PurchasePageBeansTest`: change annotation assertions from `@Dependent` to `@Stateless`, add assertions that each bean implements its `@Remote` interface.
- [x] 13.2 Update `PurchasePageBeansTest`: refactor all test methods to create Request DTOs instead of using `ServletApiTestSupport` proxies, assert on Result DTOs (check `PageAction` and model/redirectUrl).
- [x] 13.3 Simplify or remove purchase `ServletApiTestSupport` if no longer needed (beans no longer use Servlet API).
- [x] 13.4 Verify all tests pass: `mvn test`.

## 14. Final Verification

- [x] 14.1 Remove all CDI imports from JSP files (`jakarta.enterprise.inject.spi.CDI`). Verify no JSP imports CDI.
- [x] 14.2 Full build verification: `mvn clean compile`.
- [x] 14.3 Full test verification: `mvn test`.
