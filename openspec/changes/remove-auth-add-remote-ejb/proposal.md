# Why

The bookstore web application currently uses CDI (`CDI.current().select(...)`) to resolve page beans in JSP files. For a chapter-3 practice exercise, the architecture must be migrated to use `@Remote` EJB interfaces with JNDI lookup (`InitialContext.lookup(...)`) in JSP `jspInit()`/`jspDestroy()` lifecycle methods. This requires removing the Servlet API dependency from page beans (since `HttpServletRequest`/`HttpServletResponse` are not `Serializable` and cannot cross a `@Remote` boundary). Additionally, the authentication module (login, register, logout) is being removed entirely to focus solely on the purchase/booking flow with a single hardcoded customer.

## What Changes

- **BREAKING** Remove entire auth module: login/register/logout JSP pages, auth page beans, auth application service, auth domain (Customer, CustomerRepository), auth infrastructure (CustomerJpaRepository, CustomerEntity, BCryptPasswordHasher, DemoModeConfig), and all related tests.
- **BREAKING** Remove `index.jsp` (landing page) — `account/index.jsp` becomes the entry point.
- **BREAKING** Remove `require-auth.jspf` auth guard and all `<%@ include %>` references from 5 account JSPs.
- Replace `AuthSession.getCustomerId(request)` with a hardcoded `DEFAULT_CUSTOMER` constant (`CustomerId(1L)`) in all purchase page beans.
- Move `HtmlEscaper` from `auth.interfaces.web` to `shared.interfaces.web` (used by all purchase JSPs).
- Keep `Address`, `AddressRepository`, `AddressJpaRepository`, `AddressEntity`, `AddressEntityMapper` in the auth package (still needed by `CheckoutApplicationService`).
- Create `@Remote` EJB interfaces for all 5 purchase page beans: `AccountHomePage`, `CartPage`, `CheckoutPage`, `OrderHistoryPage`, `OrderDetailPage`.
- Create Serializable Request/Result DTOs for each bean to replace `HttpServletRequest`/`HttpServletResponse` parameters.
- Refactor page beans from `@Named @Dependent` CDI beans to `@Stateless` EJB beans implementing `@Remote` interfaces.
- Refactor 5 account JSP files to use `jspInit()`/`jspDestroy()` with `InitialContext.lookup("java:module/...")` instead of `CDI.current().select(...)`.
- Add `implements Serializable` to all value objects, view records, page model classes, and `AddressFormData` (required for `@Remote` serialization).
- Update `PurchasePageBeansTest` to assert `@Stateless` annotation (replacing `@Dependent`), verify `@Remote` interface implementation, and test with Request/Result DTOs instead of Servlet mocks.

## Capabilities

### New Capabilities

- `remote-ejb-page-beans`: @Remote EJB interfaces, Serializable Request/Result DTOs, JNDI lookup pattern in JSPs, and PageAction enum for controlling JSP response behavior (render, redirect).

### Modified Capabilities

(none — no existing specs)

## Impact

- **JSP files**: All 5 account JSPs rewritten (CDI → JNDI lifecycle pattern). 3 auth JSPs + index.jsp + require-auth.jspf deleted.
- **Java source**: ~30 files deleted (auth module), ~16 new files created (interfaces + DTOs), ~15 files modified (beans, models, value objects).
- **Tests**: Entire `test/auth/` directory deleted. `PurchasePageBeansTest` and `ServletApiTestSupport` rewritten to test `@Remote` bean contracts with DTOs instead of Servlet mocks.
- **Dependencies**: No new library dependencies. Existing Jakarta EJB API already in use by application services.
- **Database**: No schema changes. Address tables remain. Customer table still exists but is no longer accessed by application code.
