# ADDED Requirements

## Requirement: Customer can review the full cart during checkout

The system SHALL provide an authenticated checkout page that reviews all current cart items for the signed-in customer and SHALL only allow checkout for the full cart.

### Scenario: Checkout shows full-cart summary

- **WHEN** an authenticated customer with at least one cart item opens checkout
- **THEN** the system shows all current cart items, quantities, and totals for that cart

### Scenario: Checkout cannot proceed for an empty cart

- **WHEN** an authenticated customer opens checkout with no cart items
- **THEN** the system redirects the customer to the cart page

## Requirement: Checkout resolves shipping address from default address or checkout input

The system SHALL prefill checkout with the customer's default address when one exists. If no default address exists, the system SHALL require checkout address input and SHALL save the submitted address as the customer's new default address when the order succeeds.

### Scenario: Checkout uses existing default address

- **WHEN** an authenticated customer with a default address opens checkout
- **THEN** the system pre-populates checkout shipping information from that default address

### Scenario: Checkout requires address input when no default exists

- **WHEN** an authenticated customer without a default address opens checkout
- **THEN** the system requires shipping address fields before allowing order placement

### Scenario: Checkout input becomes the new default address

- **WHEN** an authenticated customer without a default address successfully places an order using checkout-entered shipping information
- **THEN** the system saves that shipping address as the customer's default address

## Requirement: Full-cart order placement validates stock before success

The system SHALL validate stock for every cart item at order placement time and SHALL block the full-cart order if any item cannot be fulfilled.

### Scenario: Place order succeeds for in-stock cart

- **WHEN** an authenticated customer submits checkout for a cart whose items all have sufficient stock
- **THEN** the system creates a new order with status `PLACED`, stores shipping and item snapshots, decrements stock for every ordered book, and clears the customer's cart

### Scenario: Place order fails when any item is out of stock

- **WHEN** an authenticated customer submits checkout and at least one cart item has insufficient stock
- **THEN** the system shows an inline error on checkout and leaves the customer's cart unchanged

### Scenario: Place order uses stored price snapshots without reprice flow

- **WHEN** an authenticated customer successfully places an order
- **THEN** the system stores cart and order price snapshots from the existing book prices without introducing a separate price-change confirmation flow

## Requirement: Checkout uses COD/manual payment only

The system SHALL treat checkout as a COD/manual payment flow and SHALL NOT require online payment authorization before creating an order.

### Scenario: Successful COD/manual checkout

- **WHEN** an authenticated customer submits a valid checkout request
- **THEN** the system creates the order without invoking any external payment gateway
