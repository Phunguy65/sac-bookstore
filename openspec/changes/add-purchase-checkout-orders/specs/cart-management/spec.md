# ADDED Requirements

## Requirement: Authenticated customer can manage a shopping cart

The system SHALL provide one authenticated cart per customer and SHALL allow the customer to add active books, update quantities, remove items, and review the current contents of the cart.

### Scenario: Add a book to an empty cart

- **WHEN** an authenticated customer adds an active in-stock book that is not yet in their cart
- **THEN** the system creates or reuses the customer's cart and stores a cart item for that book with the requested quantity and a unit price snapshot

### Scenario: Increase quantity for an existing cart item

- **WHEN** an authenticated customer adds a book that already exists in their cart
- **THEN** the system updates the existing cart item quantity instead of creating a duplicate cart line

### Scenario: Update quantity from the cart page

- **WHEN** an authenticated customer submits a new positive quantity for an existing cart item
- **THEN** the system persists the new quantity and shows the updated cart contents

### Scenario: Remove an item from the cart

- **WHEN** an authenticated customer removes a cart item
- **THEN** the system deletes that item from the customer's cart and shows the remaining cart contents

### Scenario: Anonymous customer attempts cart access

- **WHEN** an unauthenticated visitor requests the cart page or submits a cart mutation
- **THEN** the system redirects the visitor to the login page using the existing authentication guard behavior

### Scenario: Customer checks out with an empty cart

- **WHEN** an authenticated customer requests checkout while their cart has no items
- **THEN** the system redirects the customer back to the cart page
