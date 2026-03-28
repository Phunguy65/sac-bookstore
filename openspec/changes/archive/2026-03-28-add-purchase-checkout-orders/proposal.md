# Why

The bookstore currently has authentication in place and purchase-related domain/schema primitives, but customers still cannot add books to a cart, complete checkout, or review past orders. This change connects those existing building blocks into a usable purchasing flow so the application can move from account access to actual buying.

## What Changes

- Add an authenticated cart flow that lets customers create a cart, add books, update quantities, and remove items.
- Add a checkout flow for placing a full-cart order using COD/manual payment only.
- Use the customer's default saved address during checkout, with a fallback that allows entering a new address at checkout and saving it as the new default.
- Validate stock at order placement time, block submission with inline errors when any item is unavailable, and keep the cart unchanged on failure.
- Create placed orders with shipping snapshots, decrement stock immediately after successful order placement, and clear the cart after the full-cart order succeeds.
- Add order history and order detail pages for authenticated customers, with redirects back to history when trying to access another customer's order.

## Capabilities

### New Capabilities

- `cart-management`: Manage a customer's authenticated shopping cart, including add, update, remove, and empty-cart navigation behavior.
- `checkout-order-placement`: Review the full cart, resolve shipping address, validate stock, and place COD/manual orders.
- `customer-order-history`: List a customer's orders and show owner-scoped order details after purchase.

### Modified Capabilities

None.

## Impact

- Affected code: `src/main/java/io/github/phunguy65/bookstore/purchase/**`, `src/main/java/io/github/phunguy65/bookstore/book/**`, `src/main/java/io/github/phunguy65/bookstore/auth/**`, and `src/main/webapp/account/**`.
- Affected systems: JSP/CDI web flows, EJB application services, JPA repositories, and PostgreSQL-backed inventory/order persistence.
- Dependencies: reuses existing auth guard/session flow and existing purchase schema; no external payment gateway is introduced in this change.
