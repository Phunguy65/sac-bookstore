# ADDED Requirements

## Requirement: Remote EJB interface per page bean

Each purchase page bean SHALL have a corresponding `@Remote` interface. The interface SHALL extend `Serializable`. The interface SHALL declare a single `handle(RequestDTO)` method that accepts a Serializable request DTO and returns a Serializable result DTO. The 5 required interfaces are: `AccountHomePage`, `CartPage`, `CheckoutPage`, `OrderHistoryPage`, `OrderDetailPage`.

### Scenario: Interface declares @Remote annotation

- **WHEN** the interface class is inspected
- **THEN** it SHALL be annotated with `@jakarta.ejb.Remote`

### Scenario: Interface method accepts Serializable request

- **WHEN** the `handle` method signature is inspected
- **THEN** the parameter type SHALL implement `java.io.Serializable`

### Scenario: Interface method returns Serializable result

- **WHEN** the `handle` method signature is inspected
- **THEN** the return type SHALL implement `java.io.Serializable`

## Requirement: Stateless EJB bean implements Remote interface

Each purchase page bean SHALL be annotated with `@Stateless` (replacing `@Named @Dependent`). Each bean SHALL implement its corresponding `@Remote` interface. Each bean SHALL have a no-arg constructor (EJB spec requirement) and a parameterized constructor for dependency injection and testing.

### Scenario: Bean is annotated @Stateless

- **WHEN** the bean class is inspected
- **THEN** it SHALL be annotated with `@jakarta.ejb.Stateless`
- **THEN** it SHALL NOT be annotated with `@jakarta.enterprise.context.Dependent`
- **THEN** it SHALL NOT be annotated with `@jakarta.inject.Named`

### Scenario: Bean implements Remote interface

- **WHEN** the bean class is inspected
- **THEN** it SHALL implement its corresponding `@Remote` interface (e.g., `AccountHomePageBean implements AccountHomePage`)

### Scenario: Bean has no-arg and parameterized constructors

- **WHEN** the bean class constructors are inspected
- **THEN** there SHALL be a public no-arg constructor
- **THEN** there SHALL be a constructor accepting the bean's application service dependency

## Requirement: Serializable Request DTO per page bean

Each page bean SHALL have a dedicated Request DTO class that implements `Serializable`. The Request DTO SHALL contain all data previously extracted from `HttpServletRequest` by the bean: HTTP method, request parameters, and any query parameters. The Request DTO SHALL NOT reference any Servlet API types.

### Scenario: AccountHomePageRequest contains required fields

- **WHEN** an `AccountHomePageRequest` is created
- **THEN** it SHALL contain: `method` (String), `bookId` (String, nullable), `quantity` (String, nullable)

### Scenario: CartPageRequest contains required fields

- **WHEN** a `CartPageRequest` is created
- **THEN** it SHALL contain: `method` (String), `action` (String, nullable), `bookId` (String, nullable), `quantity` (String, nullable), `infoParam` (String, nullable)

### Scenario: CheckoutPageRequest contains required fields

- **WHEN** a `CheckoutPageRequest` is created
- **THEN** it SHALL contain: `method` (String), `recipientName` (String), `phoneNumber` (String), `line1` (String), `line2` (String), `ward` (String), `district` (String), `city` (String), `province` (String), `postalCode` (String)

### Scenario: OrderHistoryPageRequest contains required fields

- **WHEN** an `OrderHistoryPageRequest` is created
- **THEN** it SHALL contain: `pageParam` (String, nullable), `errorParam` (String, nullable)

### Scenario: OrderDetailPageRequest contains required fields

- **WHEN** an `OrderDetailPageRequest` is created
- **THEN** it SHALL contain: `orderIdParam` (String, nullable)

## Requirement: Serializable Result DTO with PageAction

Each page bean SHALL return a Result DTO class that implements `Serializable`. The Result DTO SHALL contain a `PageAction` enum value (`RENDER` or `REDIRECT`) and the page model (for RENDER) or redirect URL (for REDIRECT). The JSP SHALL use the `PageAction` to decide whether to render the page or send a redirect.

### Scenario: Result signals RENDER with model

- **WHEN** the bean returns a result with `action = RENDER`
- **THEN** the result SHALL contain a non-null page model
- **THEN** the JSP SHALL render the HTML using the model data

### Scenario: Result signals REDIRECT with URL

- **WHEN** the bean returns a result with `action = REDIRECT`
- **THEN** the result SHALL contain a non-null `redirectUrl` (relative path without context path)
- **THEN** the JSP SHALL call `response.sendRedirect(contextPath + redirectUrl)` and return

### Scenario: CheckoutPage returns REDIRECT on empty cart

- **WHEN** a GET request is made to checkout and the cart is empty
- **THEN** the result SHALL have `action = REDIRECT` with URL pointing to cart page with `info=emptyCheckout`

### Scenario: CheckoutPage returns REDIRECT on successful order

- **WHEN** a POST request places an order successfully
- **THEN** the result SHALL have `action = REDIRECT` with URL pointing to order detail page with the new order ID

### Scenario: OrderDetailPage returns REDIRECT on missing order

- **WHEN** a request is made for a non-existent or invalid order ID
- **THEN** the result SHALL have `action = REDIRECT` with URL pointing to orders page with `error=missing`

## Requirement: JNDI lookup in JSP jspInit/jspDestroy

Each account JSP file SHALL obtain its page bean reference via JNDI lookup in `jspInit()` and release it in `jspDestroy()`. The JSP SHALL use `InitialContext.lookup("java:module/BeanClassName!fully.qualified.InterfaceName")` as the JNDI name. The JSP SHALL NOT import or use `jakarta.enterprise.inject.spi.CDI`.

### Scenario: JSP declares jspInit with JNDI lookup

- **WHEN** the JSP source is inspected
- **THEN** it SHALL contain a `<%! %>` declaration block with a private field typed to the `@Remote` interface
- **THEN** `jspInit()` SHALL create an `InitialContext` and call `lookup(...)` with the portable JNDI name
- **THEN** the looked-up object SHALL be cast to the `@Remote` interface type

### Scenario: JSP declares jspDestroy

- **WHEN** the JSP source is inspected
- **THEN** `jspDestroy()` SHALL set the bean field to `null`

### Scenario: JSP extracts parameters into Request DTO

- **WHEN** the JSP scriptlet executes before rendering
- **THEN** it SHALL create a Request DTO from `request.getMethod()` and `request.getParameter(...)` calls
- **THEN** it SHALL call `bean.handle(requestDTO)` to get the Result DTO

### Scenario: JSP handles REDIRECT action

- **WHEN** the Result DTO action is `REDIRECT`
- **THEN** the JSP SHALL call `response.sendRedirect(request.getContextPath() + result.getRedirectUrl())`
- **THEN** the JSP SHALL return immediately (no HTML rendering)

### Scenario: JNDI lookup failure is logged

- **WHEN** `InitialContext.lookup(...)` throws an exception in `jspInit()`
- **THEN** the exception message SHALL be printed to stdout
- **THEN** the bean field SHALL remain `null`

## Requirement: All types in Remote interface graph are Serializable

Every Java type that appears as a parameter, return type, or field within the Request DTO, Result DTO, page model, view record, or value object graph SHALL implement `java.io.Serializable`. This includes all records in `shared.domain.valueobject`, `book.domain.valueobject`, `purchase.application.service` view records, all `PageModel` classes, and `AddressFormData`.

### Scenario: Value object records implement Serializable

- **WHEN** any value object record (e.g., `Money`, `BookId`, `Quantity`, `BookTitle`) is inspected
- **THEN** it SHALL implement `java.io.Serializable`

### Scenario: Application view records implement Serializable

- **WHEN** any application view record (e.g., `CartView`, `CartLineView`, `CatalogBookView`, `OrderSummaryView`, `OrderDetailView`) is inspected
- **THEN** it SHALL implement `java.io.Serializable`

### Scenario: Page model classes implement Serializable

- **WHEN** any page model class (e.g., `AccountHomePageModel`, `CartPageModel`, `CheckoutPageModel`) is inspected
- **THEN** it SHALL implement `java.io.Serializable`

### Scenario: AddressFormData implements Serializable

- **WHEN** `AddressFormData` is inspected
- **THEN** it SHALL implement `java.io.Serializable`

## Requirement: Hardcoded default customer identity

With auth removed, all purchase page beans SHALL use a single hardcoded `CustomerId` constant (`DEFAULT_CUSTOMER` with value `1L`) defined on the `CustomerId` record. Page beans SHALL NOT reference `AuthSession` or any auth session mechanism.

### Scenario: CustomerId defines DEFAULT_CUSTOMER constant

- **WHEN** the `CustomerId` record is inspected
- **THEN** it SHALL have a `public static final CustomerId DEFAULT_CUSTOMER = new CustomerId(1L)` field

### Scenario: Page beans use DEFAULT_CUSTOMER

- **WHEN** any purchase page bean needs a customer identity
- **THEN** it SHALL use `CustomerId.DEFAULT_CUSTOMER`
- **THEN** it SHALL NOT import or reference `AuthSession`

## Requirement: Auth module removal

All auth-specific code SHALL be removed: auth JSP pages (`login.jsp`, `register.jsp`, `logout.jsp`), auth page beans (`LoginPageBean`, `RegisterPageBean`, `LogoutPageBean`, `AuthGuardBean`), auth models (`LoginPageModel`, `RegisterPageModel`), auth utilities (`AuthSession`, `AuthPaths`), auth application services (`AuthApplicationService`, `PasswordHasher`, `BCryptPasswordHasher`, `DemoModeSettings`, `DemoModeConfig`, `LoginResult`, `RegisterResult`), auth domain (`Customer`, `CustomerStatus`, `CustomerRepository`), auth infrastructure (`CustomerJpaRepository`, `CustomerEntityMapper`), `require-auth.jspf`, and `index.jsp`. The `HtmlEscaper` utility SHALL be moved to `shared.interfaces.web` before deletion of the auth web package. Address-related code (`Address`, `AddressRepository`, `AddressJpaRepository`, `AddressEntity`, `AddressEntityMapper`) SHALL be kept in the auth package. `CustomerEntity` SHALL be kept for JPA FK integrity.

### Scenario: Auth JSP pages are deleted

- **WHEN** the webapp directory is inspected
- **THEN** `auth/login.jsp`, `auth/register.jsp`, `auth/logout.jsp`, `index.jsp`, and `WEB-INF/jspf/auth/require-auth.jspf` SHALL NOT exist

### Scenario: Auth include directives are removed from account JSPs

- **WHEN** any account JSP (`index.jsp`, `cart.jsp`, `checkout.jsp`, `orders.jsp`, `order-detail.jsp`) is inspected
- **THEN** it SHALL NOT contain `<%@ include file="/WEB-INF/jspf/auth/require-auth.jspf" %>`

### Scenario: Auth Java classes are deleted

- **WHEN** the auth source directory is inspected
- **THEN** `LoginPageBean`, `RegisterPageBean`, `LogoutPageBean`, `AuthGuardBean`, `AuthSession`, `AuthPaths`, `LoginPageModel`, `RegisterPageModel`, `AuthApplicationService`, `PasswordHasher`, `BCryptPasswordHasher`, `DemoModeSettings`, `DemoModeConfig`, `LoginResult`, `RegisterResult`, `Customer`, `CustomerStatus`, `CustomerRepository`, `CustomerJpaRepository`, `CustomerEntityMapper` SHALL NOT exist

### Scenario: Auth test classes are deleted

- **WHEN** the test auth directory is inspected
- **THEN** `AuthPageBeansTest`, `AuthSessionTest`, `LoginResultTest`, `RegisterResultTest`, `AuthApplicationServiceTest`, `BCryptPasswordHasherTest`, `DemoModeConfigTest`, `DemoModeConfigurationAlignmentTest`, `CustomerEntityMapperTest`, and auth `ServletApiTestSupport` SHALL NOT exist

### Scenario: HtmlEscaper is relocated to shared

- **WHEN** `HtmlEscaper` is needed
- **THEN** it SHALL be at `shared.interfaces.web.HtmlEscaper`
- **THEN** it SHALL NOT exist at `auth.interfaces.web.HtmlEscaper`

### Scenario: Address code is preserved

- **WHEN** the auth domain and infrastructure are inspected
- **THEN** `Address`, `AddressRepository`, `AddressJpaRepository`, `AddressEntity`, `AddressEntityMapper` SHALL still exist in their current auth packages

### Scenario: CustomerEntity is preserved for FK integrity

- **WHEN** the auth infrastructure persistence entity package is inspected
- **THEN** `CustomerEntity` SHALL still exist (required by AddressEntity JPA relationship)

## Requirement: Account index JSP cleanup

The `account/index.jsp` page SHALL remove all auth-related UI elements: the logout form/button, the "Ve trang chu" link (since `index.jsp` is deleted). The page SHALL NOT import `AuthSession`.

### Scenario: Logout form is removed

- **WHEN** `account/index.jsp` is inspected
- **THEN** it SHALL NOT contain a logout form or logout button

### Scenario: Home page link is removed

- **WHEN** `account/index.jsp` is inspected
- **THEN** it SHALL NOT contain a link to `index.jsp`

### Scenario: AuthSession import is removed

- **WHEN** `account/index.jsp` imports are inspected
- **THEN** it SHALL NOT import `AuthSession`

## Requirement: Updated test assertions

`PurchasePageBeansTest` SHALL be updated to verify: beans are `@Stateless` (not `@Dependent`), beans implement their `@Remote` interface, beans accept Request DTOs and return Result DTOs. `ServletApiTestSupport` SHALL be simplified to remove `AuthSession` references. The `authenticated()` helper method can remain for backward compatibility but SHALL NOT use `AuthSession`.

### Scenario: Tests assert @Stateless annotation

- **WHEN** the test verifies bean scope
- **THEN** it SHALL assert the bean class is annotated with `@jakarta.ejb.Stateless`
- **THEN** it SHALL assert the bean class is NOT annotated with `@jakarta.enterprise.context.Dependent`

### Scenario: Tests assert Remote interface implementation

- **WHEN** the test verifies bean interface
- **THEN** it SHALL assert the bean class implements its corresponding `@Remote` interface

### Scenario: Tests use Request DTOs instead of Servlet mocks

- **WHEN** the test invokes a bean
- **THEN** it SHALL create a Request DTO with the appropriate fields
- **THEN** it SHALL call `bean.handle(requestDTO)` and inspect the returned Result DTO
- **THEN** it SHALL verify `PageAction` and model/redirectUrl as appropriate
