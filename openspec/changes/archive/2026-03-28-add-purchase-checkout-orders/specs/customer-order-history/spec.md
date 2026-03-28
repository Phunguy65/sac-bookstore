# ADDED Requirements

## Requirement: Customer can view their order history

The system SHALL provide an authenticated order history page that lists orders belonging to the signed-in customer.

### Scenario: Order history shows the customer's orders

- **WHEN** an authenticated customer opens the order history page
- **THEN** the system lists orders that belong to that customer

### Scenario: Anonymous visitor attempts to open order history

- **WHEN** an unauthenticated visitor requests the order history page
- **THEN** the system redirects the visitor to the login page using the existing authentication guard behavior

## Requirement: Customer can view owner-scoped order details

The system SHALL provide an authenticated order detail page that shows one order and its items only when the requested order belongs to the signed-in customer.

### Scenario: Order detail shows owned order

- **WHEN** an authenticated customer opens an order detail page for one of their own orders
- **THEN** the system shows the order's shipping snapshot, status, totals, and item details

### Scenario: Customer requests another customer's order

- **WHEN** an authenticated customer requests an order detail page for an order they do not own
- **THEN** the system redirects the customer to order history and shows an inline error message

### Scenario: Customer requests a missing order

- **WHEN** an authenticated customer requests an order detail page for an order that cannot be found in their visible scope
- **THEN** the system redirects the customer to order history and shows an inline error message
