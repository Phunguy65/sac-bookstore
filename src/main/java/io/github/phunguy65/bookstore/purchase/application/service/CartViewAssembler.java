package io.github.phunguy65.bookstore.purchase.application.service;

import io.github.phunguy65.bookstore.book.domain.model.Book;
import io.github.phunguy65.bookstore.book.domain.repository.BookRepository;
import io.github.phunguy65.bookstore.purchase.domain.model.Cart;
import io.github.phunguy65.bookstore.purchase.domain.model.CartItem;
import io.github.phunguy65.bookstore.shared.domain.valueobject.BookId;
import io.github.phunguy65.bookstore.shared.domain.valueobject.Money;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class CartViewAssembler {
    private CartViewAssembler() {
    }

    static CartView toCartView(Cart cart, BookRepository bookRepository) {
        Set<BookId> bookIds = cart.items().stream().map(CartItem::bookId).collect(java.util.stream.Collectors.toSet());
        Map<BookId, Book> booksById = new HashMap<>();
        for (Book book : bookRepository.findByIds(bookIds)) {
            booksById.put(book.id(), book);
        }

        List<CartLineView> lines = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : cart.items()) {
            Book book = booksById.get(item.bookId());
            if (book == null) {
                throw new IllegalStateException("book not found for cart item");
            }
            Money lineTotal = new Money(item.unitPriceSnapshot().amount().multiply(BigDecimal.valueOf(item.quantity().value())));
            total = total.add(lineTotal.amount());
            lines.add(new CartLineView(item.bookId(), book.title(), item.unitPriceSnapshot(), item.quantity(), lineTotal, book.stockQuantity()));
        }
        return new CartView(lines, new Money(total));
    }
}
