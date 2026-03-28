# Tasks

## 1. Persistence adapters and query support

- [x] 1.1 Implement JPA repository adapters for books, addresses, carts, and orders using the existing domain models and mappers.
- [x] 1.2 Add repository query behavior needed for purchase flows, including default-address lookup, customer cart lookup, customer order listing, and owner-scoped order detail loading.

## 2. Purchase application services

- [x] 2.1 Implement a cart application service that supports add, update, remove, and cart retrieval for the authenticated customer.
- [x] 2.2 Implement a checkout/order placement service that validates full-cart stock, resolves shipping address rules, creates `PLACED` orders, decrements stock, and clears the cart on success.
- [x] 2.3 Implement an order query service for customer order history and owner-scoped order detail retrieval with redirect/error outcomes.

## 3. Purchase web flows and pages

- [x] 3.1 Add protected cart page and page bean flows for viewing the cart and mutating item quantities.
- [x] 3.2 Add protected checkout page and page bean flow for full-cart review, default-address prefilling, fallback address entry, inline checkout errors, and success redirect.
- [x] 3.3 Add protected order history and order detail pages with redirect-and-error behavior for missing or foreign orders.

## 4. Verification and automated tests

- [x] 4.1 Add unit tests for cart, checkout, and order query service rules, including stock failure and address fallback scenarios.
- [x] 4.2 Add integration tests for repository behavior and transactional order placement side effects, including stock decrement and cart clearing.
- [x] 4.3 Run `./gradlew test` and fix any failures related to the new purchase flow.
