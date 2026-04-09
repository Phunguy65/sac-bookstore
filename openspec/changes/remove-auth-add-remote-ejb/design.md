# Context

The bookstore is a Jakarta EE web application using JSP for views and CDI/EJB for business logic. Currently, JSP files obtain page beans via `CDI.current().select(BeanClass.class).get()` -- a programmatic CDI lookup. The application has two modules: auth (login/register/logout) and purchase (catalog, cart, checkout, orders).

The course exercise requires migrating from CDI programmatic lookup to the `@Remote` EJB + JNDI pattern where JSP pages use `jspInit()`/`jspDestroy()` lifecycle methods to obtain bean references via `InitialContext.lookup(...)`. This is the classic Java EE pattern demonstrated by the `Converter`/`ConverterBean` code sample.

Current state:
- 9 page beans: 4 auth + 5 purchase
- All page beans are `@Named @Dependent` CDI beans with constructor injection
- All page beans accept `HttpServletRequest`/`HttpServletResponse` as method parameters
- Application services are already `@Stateless` EJB beans
- `HtmlEscaper` utility lives in `auth.interfaces.web` but is used by all purchase JSPs

## Goals / Non-Goals

**Goals:**

- Remove the entire auth module (login, register, logout, auth guard) to simplify the application to purchase-only
- Replace CDI programmatic lookup with `@Remote` EJB interfaces + JNDI lookup in JSP `jspInit()`/`jspDestroy()`
- Remove `HttpServletRequest`/`HttpServletResponse` from page bean method signatures (not Serializable, cannot cross `@Remote` boundary)
- Maintain full test coverage for purchase page beans using DTOs instead of Servlet mocks
- All value objects, view records, page models, and DTOs must be Serializable for `@Remote` transport

**Non-Goals:**

- Changing the purchase business logic or application services
- Migrating application services to use `@Remote` interfaces (they are already `@Stateless` EJB, called internally)
- Moving Address/AddressRepository out of the auth package (still works, minimal disruption)
- Database schema changes
- Adding new features or UI changes beyond removing auth-related UI elements

## Decisions

### D1: Use `@Remote` interfaces (not `@Local`)

**Decision**: Page bean EJB interfaces use `@Remote`.

**Rationale**: The course exercise code sample (`Converter`/`ConverterBean`) uses `@Remote`. This is the required pattern. `@Remote` requires all parameters and return types to be `Serializable`, which forces a clean separation between web tier (Servlet API) and business tier (EJB).

**Alternative considered**: `@Local` would avoid the Serializable requirement but would not match the exercise pattern.

### D2: Refactor bean method signatures to use Serializable DTOs

**Decision**: Each page bean gets a dedicated Request DTO (input) and Result DTO (output), both `Serializable`. The Result contains a `PageAction` enum telling the JSP what to do (RENDER, REDIRECT, etc.).

**Rationale**: `HttpServletRequest`/`HttpServletResponse` are not Serializable. Since page beans currently read request parameters, check HTTP method, manage sessions, and send redirects, these responsibilities must be split: parameter extraction and response handling stay in the JSP; pure logic moves to the bean.

**Alternative considered**: Passing individual primitive parameters instead of DTOs. Rejected because some beans need 9+ parameters (CheckoutPageBean) and the Result needs to convey both data and action signals.

### D3: PageAction enum for response control

**Decision**: Create a `PageAction` enum with values: `RENDER`, `REDIRECT`. The Result DTO includes the action and an optional `redirectUrl`.

**Rationale**: Page beans currently call `response.sendRedirect(...)` directly. With `@Remote`, the bean cannot touch the response. Instead, it returns an action signal, and the JSP dispatches accordingly. Two actions (RENDER and REDIRECT) is sufficient -- login session management is removed with auth, and all remaining beans either render a page or redirect.

### D4: Portable JNDI lookup name

**Decision**: Use `java:module/BeanClassName!fully.qualified.InterfaceName` for JNDI lookup.

**Rationale**: The `java:module/` prefix is the portable Jakarta EE JNDI namespace that works across application servers. It resolves to EJBs within the same module (WAR/EAR). The full interface FQCN is required when a bean implements a `@Remote` interface.

### D5: Hardcoded DEFAULT_CUSTOMER constant

**Decision**: Add `public static final CustomerId DEFAULT_CUSTOMER = new CustomerId(1L)` as a constant on `CustomerId`. All purchase page beans use this instead of `AuthSession.getCustomerId(request)`.

**Rationale**: With auth removed, there is no session-based identity. A single hardcoded customer simplifies the purchase flow for the exercise.

### D6: HtmlEscaper moves to shared.interfaces.web

**Decision**: Relocate `HtmlEscaper` from `auth.interfaces.web` to `shared.interfaces.web`.

**Rationale**: `HtmlEscaper` is a general-purpose utility used by all 5 purchase JSPs. It has no auth-specific logic. Moving it to shared prevents a dependency from purchase JSPs into the auth package (which is being partially deleted).

### D7: Keep Address-related code in auth package

**Decision**: `Address`, `AddressRepository`, `AddressJpaRepository`, `AddressEntity`, `AddressEntityMapper` remain in `auth.*` packages.

**Rationale**: `CheckoutApplicationService` depends on `AddressRepository` for shipping address resolution. Moving these classes would require changing package declarations, imports, and JPA entity mappings -- high risk for no functional benefit.

### D8: Bean retains constructor for testability

**Decision**: Each `@Stateless` bean keeps both a no-arg constructor (required by EJB spec) and a parameterized constructor (for unit testing without a container).

**Rationale**: Existing tests create beans directly via `new BeanClass(stubService)`. This pattern remains valid. Field injection (`@Inject`) is used by the container; the parameterized constructor is used by tests.

## Risks / Trade-offs

- **[Serializable cascade]** Adding `Serializable` to ~30 records/classes is tedious but mechanical. Risk: missing one causes runtime `NotSerializableException`. Mitigation: compile-time review of all types reachable from Request/Result DTOs.

- **[JNDI lookup failure at startup]** If the JNDI name is wrong, `jspInit()` silently catches the exception and sets the bean to `null`, causing `NullPointerException` on first request. Mitigation: error is logged in catch block; integration testing on deployment catches this.

- **[Address FK to CustomerEntity]** `AddressEntity` likely has a JPA `@ManyToOne` to `CustomerEntity`. Deleting `CustomerEntity` would break JPA. Mitigation: keep `CustomerEntity` in the codebase -- only delete Customer domain model, repository interface, and JPA repository implementation. The JPA entity stays for FK integrity.

- **[Test refactor scope]** `PurchasePageBeansTest` currently uses Servlet proxies and `AuthSession`. After refactor, beans accept DTOs so tests become simpler. But the rewrite is non-trivial. Mitigation: refactor tests incrementally per bean.
