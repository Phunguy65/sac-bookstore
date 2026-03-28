<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="io.github.phunguy65.bookstore.auth.interfaces.web.HtmlEscaper" %>
<%@ page import="io.github.phunguy65.bookstore.purchase.application.service.CartLineView" %>
<%@ page import="io.github.phunguy65.bookstore.purchase.interfaces.web.CartPageBean" %>
<%@ page import="io.github.phunguy65.bookstore.purchase.interfaces.web.CartPageModel" %>
<%@ page import="jakarta.enterprise.inject.spi.CDI" %>
<%@ include file="/WEB-INF/jspf/auth/require-auth.jspf" %>
<%
    CartPageModel form = CDI.current().select(CartPageBean.class).get().handle(request, response);
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gio hang - Bookstore</title>
    <style>
        :root { color-scheme: light; --bg:#f4ecdf; --panel:#fffaf3; --border:#d9c7b1; --text:#2f271e; --muted:#6d6258; --accent:#8b4f29; --accent-strong:#6d3718; --on-accent:#fffaf3; --error-bg:#f9dfd8; --error-text:#842029; --info-bg:#e7f4ea; --info-text:#295b34; }
        * { box-sizing:border-box; }
        body { margin:0; min-height:100vh; font-family:Georgia, "Times New Roman", serif; background:linear-gradient(180deg,#f9f5ef 0%,#f0e5d7 100%); color:var(--text); padding:24px; }
        .shell { max-width:980px; margin:0 auto; background:var(--panel); border:1px solid var(--border); border-radius:24px; padding:28px; }
        .row, .actions { display:flex; gap:12px; flex-wrap:wrap; align-items:center; }
        .actions { margin-top:20px; }
        .notice { border-radius:12px; padding:12px 14px; margin:16px 0; }
        .notice.error { background:var(--error-bg); color:var(--error-text); }
        .notice.info { background:var(--info-bg); color:var(--info-text); }
        table { width:100%; border-collapse:collapse; margin-top:18px; }
        th, td { padding:14px 10px; border-top:1px solid var(--border); text-align:left; vertical-align:top; }
        input { min-height:44px; border-radius:12px; border:1px solid var(--border); padding:10px 12px; font:inherit; }
        .quantity { width:88px; }
        .book-id { width:120px; }
        button, a.link, a.primary { min-height:44px; border-radius:999px; padding:12px 18px; font:inherit; text-decoration:none; display:inline-flex; align-items:center; justify-content:center; }
        button.primary, a.primary { border:0; background:var(--accent); color:var(--on-accent); }
        button.secondary, a.link { border:1px solid var(--border); background:transparent; color:var(--text); }
        .muted { color:var(--muted); }
        a:focus-visible, button:focus-visible, input:focus-visible { outline:2px solid var(--accent); outline-offset:3px; }
        .sr-only { position:absolute; width:1px; height:1px; padding:0; margin:-1px; overflow:hidden; clip:rect(0, 0, 0, 0); white-space:nowrap; border:0; }
        button:disabled { opacity:.65; cursor:not-allowed; }
        .toolbar { display:flex; justify-content:space-between; gap:12px; flex-wrap:wrap; align-items:center; }
        .total-bar { display:flex; justify-content:space-between; gap:12px; flex-wrap:wrap; align-items:center; }
        .empty-state { margin-top:24px; display:flex; gap:12px; flex-wrap:wrap; align-items:center; }
        .field-error { color:var(--error-text); font-size:.95rem; margin-top:6px; }
        @media (max-width: 760px) { table, thead, tbody, tr, td, th { display:block; } thead { display:none; } td { padding:10px 0; } td::before { content: attr(data-label) ": "; display:block; font-weight:700; margin-bottom:4px; } }
    </style>
</head>
<body>
<main class="shell">
    <div class="toolbar">
        <div>
            <h1>Gio hang</h1>
            <p class="muted">Them sach theo ma sach, cap nhat so luong va chuan bi dat don COD.</p>
        </div>
        <div class="actions">
            <a class="link" href="<%= request.getContextPath() %>/account/index.jsp">Tai khoan</a>
            <a class="primary" href="<%= request.getContextPath() %>/account/checkout.jsp">Thanh toan</a>
        </div>
    </div>

    <% if (form.getErrorMessage() != null) { %>
    <div class="notice error" role="alert"><%= HtmlEscaper.escape(form.getErrorMessage()) %></div>
    <% } %>
    <% if (form.getInfoMessage() != null) { %>
    <div class="notice info" role="status"><%= HtmlEscaper.escape(form.getInfoMessage()) %></div>
    <% } %>

    <form method="post" class="row" onsubmit="const btn=this.querySelector('button[type=submit]'); const status=document.getElementById('cart-add-status'); if (btn) { btn.disabled=true; btn.innerText='Dang xu ly...'; } if (status) { status.innerText='Dang xu ly...'; }">
        <input type="hidden" name="action" value="add">
        <label class="sr-only" for="add-book-id">Ma sach</label>
        <input id="add-book-id" class="book-id" type="number" min="1" name="bookId" placeholder="Ma sach" required aria-label="Ma sach" <%= form.getAddBookIdError() != null ? "aria-invalid=\"true\" aria-describedby=\"add-book-id-error\"" : "" %>>
        <label class="sr-only" for="add-quantity">So luong</label>
        <input id="add-quantity" class="quantity" type="number" min="1" name="quantity" value="1" required aria-label="So luong" <%= form.getAddQuantityError() != null ? "aria-invalid=\"true\" aria-describedby=\"add-quantity-error\"" : "" %>>
        <button class="primary" type="submit">Them vao gio</button>
    </form>
    <% if (form.getAddBookIdError() != null) { %><div id="add-book-id-error" class="field-error"><%= HtmlEscaper.escape(form.getAddBookIdError()) %></div><% } %>
    <% if (form.getAddQuantityError() != null) { %><div id="add-quantity-error" class="field-error"><%= HtmlEscaper.escape(form.getAddQuantityError()) %></div><% } %>
    <span id="cart-add-status" class="sr-only" aria-live="polite"></span>

    <% if (form.getCart().isEmpty()) { %>
    <div class="empty-state">
        <p class="muted">Gio hang cua ban dang trong.</p>
        <a class="link" href="<%= request.getContextPath() %>/account/index.jsp">Ve tai khoan</a>
    </div>
    <% } else { %>
    <table>
        <thead>
        <tr>
            <th>Sach</th>
            <th>Don gia</th>
            <th>So luong</th>
            <th>Tam tinh</th>
            <th></th>
        </tr>
        </thead>
        <tbody>
        <% for (CartLineView item : form.getCart().items()) { %>
        <tr>
            <td data-label="Sach">
                <strong><%= HtmlEscaper.escape(item.bookTitle().value()) %></strong><br>
                <span class="muted">Ma sach: <%= item.bookId().value() %> - Ton kho: <%= item.availableStock().value() %></span>
            </td>
            <td data-label="Don gia"><%= item.unitPrice().amount() %></td>
            <td data-label="So luong">
                <form method="post" class="row" onsubmit="const btn=this.querySelector('button[type=submit]'); if (btn) { btn.disabled=true; btn.innerText='Dang xu ly...'; }">
                    <input type="hidden" name="action" value="update">
                    <input type="hidden" name="bookId" value="<%= item.bookId().value() %>">
                    <label class="sr-only" for="quantity-<%= item.bookId().value() %>">So luong sach <%= item.bookId().value() %></label>
                    <input id="quantity-<%= item.bookId().value() %>" class="quantity" type="number" min="1" name="quantity" value="<%= item.quantity().value() %>" required aria-label="So luong sach <%= item.bookId().value() %>" <%= form.getLineQuantityError() != null && form.getLineErrorBookId() != null && form.getLineErrorBookId().equals(item.bookId().value()) ? "aria-invalid=\"true\" aria-describedby=\"line-quantity-error-" + item.bookId().value() + "\"" : "" %>>
                    <button class="secondary" type="submit">Cap nhat</button>
                    <% if (form.getLineQuantityError() != null && form.getLineErrorBookId() != null && form.getLineErrorBookId().equals(item.bookId().value())) { %>
                    <div id="line-quantity-error-<%= item.bookId().value() %>" class="field-error"><%= HtmlEscaper.escape(form.getLineQuantityError()) %></div>
                    <% } %>
                </form>
            </td>
            <td data-label="Tam tinh"><%= item.lineTotal().amount() %></td>
            <td data-label="Thao tac">
                <form method="post" onsubmit="const btn=this.querySelector('button[type=submit]'); const status=document.getElementById('cart-remove-status-<%= item.bookId().value() %>'); if (btn) { btn.disabled=true; btn.innerText='Dang xu ly...'; } if (status) { status.innerText='Dang xu ly...'; }">
                    <input type="hidden" name="action" value="remove">
                    <input type="hidden" name="bookId" value="<%= item.bookId().value() %>">
                    <button class="secondary" type="submit">Xoa</button>
                    <span id="cart-remove-status-<%= item.bookId().value() %>" class="sr-only" aria-live="polite"></span>
                </form>
            </td>
        </tr>
        <% } %>
        </tbody>
    </table>

    <div class="actions total-bar">
        <strong>Tong tam tinh: <%= form.getCart().totalAmount().amount() %></strong>
        <a class="primary" href="<%= request.getContextPath() %>/account/checkout.jsp">Tiep tuc thanh toan</a>
    </div>
    <% } %>
</main>
</body>
</html>
