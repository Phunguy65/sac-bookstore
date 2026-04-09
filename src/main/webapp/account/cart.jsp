<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="io.github.phunguy65.bookstore.shared.interfaces.web.HtmlEscaper" %>
<%@ page import="io.github.phunguy65.bookstore.purchase.application.service.CartLineView" %>
<%@ page import="io.github.phunguy65.bookstore.purchase.interfaces.web.CartPage" %>
<%@ page import="io.github.phunguy65.bookstore.purchase.interfaces.web.CartPageRequest" %>
<%@ page import="io.github.phunguy65.bookstore.purchase.interfaces.web.CartPageResult" %>
<%@ page import="io.github.phunguy65.bookstore.purchase.interfaces.web.CartPageModel" %>
<%@ page import="io.github.phunguy65.bookstore.purchase.interfaces.web.PageAction" %>
<%@ page import="javax.naming.InitialContext" %>
<%!
    private CartPage cartPage = null;

    public void jspInit() {
        try {
            InitialContext ic = new InitialContext();
            cartPage = (CartPage) ic.lookup("java:module/CartPageBean!io.github.phunguy65.bookstore.purchase.interfaces.web.CartPage");
        } catch (Exception ex) {
            System.out.println("Could not create CartPage bean. " + ex.getMessage());
        }
    }

    public void jspDestroy() {
        cartPage = null;
    }
%>
<%
    CartPageRequest pageRequest = new CartPageRequest(
        request.getMethod(),
        request.getParameter("action"),
        request.getParameter("bookId"),
        request.getParameter("quantity"),
        request.getParameter("info")
    );
    CartPageResult result = cartPage.handle(pageRequest);
    CartPageModel form = result.model();
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gio hang - Bookstore</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/purchase-shared.css">
    <style>
        .row { display:flex; gap:12px; flex-wrap:wrap; align-items:center; }
        .actions { margin-top:20px; }
        .row .field-error { width:100%; }
    </style>
</head>
<body class="purchase-page">
<main class="shell purchase-shell-medium purchase-shell-shadow">
    <div class="toolbar">
        <div>
            <h1>Gio hang</h1>
            <p class="muted">Ra soat cac sach da chon, cap nhat so luong va chuan bi dat don COD.</p>
        </div>
        <div class="actions">
            <a class="link" href="<%= request.getContextPath() %>/account/index.jsp">Chon sach</a>
            <% if (!form.getCart().isEmpty()) { %>
            <a class="primary" href="<%= request.getContextPath() %>/account/checkout.jsp">Thanh toan</a>
            <% } %>
        </div>
    </div>

    <% if (form.getErrorMessage() != null) { %>
    <div class="notice error" role="alert"><%= HtmlEscaper.escape(form.getErrorMessage()) %></div>
    <% } %>
    <% if (form.getInfoMessage() != null) { %>
    <div class="notice info" role="status"><%= HtmlEscaper.escape(form.getInfoMessage()) %></div>
    <% } %>

    <div class="panel">
        <p class="muted">Muon them sach moi? Hay quay lai trang tai khoan de chon sach tu danh muc hien co.</p>
        <a class="link" href="<%= request.getContextPath() %>/account/index.jsp">Mo danh muc sach</a>
    </div>

    <% if (form.getCart().isEmpty()) { %>
    <div class="empty-state">
        <p class="muted">Gio hang cua ban dang trong.</p>
        <a class="link" href="<%= request.getContextPath() %>/account/index.jsp">Chon sach ngay</a>
    </div>
    <% } else { %>
    <table class="purchase-table">
        <thead>
        <tr>
            <th>Sach</th>
            <th>Don gia</th>
            <th>So luong</th>
            <th>Tam tinh</th>
            <th>Thao tac</th>
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
                <form method="post" class="row" onsubmit="const btn=this.querySelector('button[type=submit]'); const status=document.getElementById('cart-update-status-<%= item.bookId().value() %>'); if (btn) { btn.disabled=true; btn.innerText='Dang xu ly...'; } if (status) { status.innerText='Dang xu ly...'; }">
                    <input type="hidden" name="action" value="update">
                    <input type="hidden" name="bookId" value="<%= item.bookId().value() %>">
                    <label class="sr-only" for="quantity-<%= item.bookId().value() %>">So luong sach <%= item.bookId().value() %></label>
                    <input id="quantity-<%= item.bookId().value() %>" class="purchase-input purchase-width-quantity" type="number" min="1" name="quantity" value="<%= item.quantity().value() %>" required aria-label="So luong sach <%= item.bookId().value() %>" <%= form.getLineQuantityError() != null && form.getLineErrorBookId() != null && form.getLineErrorBookId().equals(item.bookId().value()) ? "aria-invalid=\"true\" aria-describedby=\"line-quantity-error-" + item.bookId().value() + "\"" : "" %>>
                    <button class="secondary" type="submit">Cap nhat</button>
                    <% if (form.getLineQuantityError() != null && form.getLineErrorBookId() != null && form.getLineErrorBookId().equals(item.bookId().value())) { %>
                    <div id="line-quantity-error-<%= item.bookId().value() %>" class="field-error"><%= HtmlEscaper.escape(form.getLineQuantityError()) %></div>
                    <% } %>
                    <span id="cart-update-status-<%= item.bookId().value() %>" class="sr-only" aria-live="polite"></span>
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
