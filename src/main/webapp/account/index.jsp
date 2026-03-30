<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="io.github.phunguy65.bookstore.auth.interfaces.web.AuthSession" %>
<%@ page import="io.github.phunguy65.bookstore.auth.interfaces.web.HtmlEscaper" %>
<%@ page import="io.github.phunguy65.bookstore.purchase.application.service.CatalogBookView" %>
<%@ page import="io.github.phunguy65.bookstore.purchase.interfaces.web.AccountHomePageBean" %>
<%@ page import="io.github.phunguy65.bookstore.purchase.interfaces.web.AccountHomePageModel" %>
<%@ page import="jakarta.enterprise.inject.spi.CDI" %>
<%@ include file="/WEB-INF/jspf/auth/require-auth.jspf" %>
<%
    AccountHomePageModel form = CDI.current().select(AccountHomePageBean.class).get().handle(request, response);
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tai khoan - Bookstore</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/purchase-shared.css">
    <style>
        .catalog-grid { display:grid; gap:18px; margin-top:24px; }
        .catalog-card { display:grid; grid-template-columns:minmax(0, 1fr) auto; gap:18px; align-items:start; }
        .catalog-meta { display:flex; flex-wrap:wrap; gap:10px 14px; margin-top:12px; }
        .catalog-meta span { color:var(--muted); }
        .catalog-description { margin-top:12px; line-height:1.6; }
        .catalog-form { min-width:220px; display:flex; flex-direction:column; gap:10px; align-items:stretch; }
        .catalog-form .field-error { margin-top:0; }
        .catalog-out-of-stock { font-weight:700; color:var(--error-text); }
        @media (max-width: 760px) {
            .catalog-card { grid-template-columns:1fr; }
            .catalog-form { min-width:0; }
        }
    </style>
</head>
<body class="purchase-page purchase-page-soft">
<main class="shell purchase-shell-wide purchase-shell-shadow">
    <div class="toolbar">
        <div>
            <h1>Xin chao ban</h1>
            <p class="muted">Chon sach tu danh muc ben duoi, them vao gio hang va tiep tuc dat don COD.</p>
        </div>
        <div class="actions">
            <a class="link" href="<%= request.getContextPath() %>/index.jsp">Ve trang chu</a>
            <a class="link" href="<%= request.getContextPath() %>/account/cart.jsp">Mo gio hang</a>
            <a class="link" href="<%= request.getContextPath() %>/account/orders.jsp">Xem don hang</a>
            <form method="post" action="<%= request.getContextPath() %>/auth/logout.jsp" onsubmit="const btn=this.querySelector('button[type=submit]'); const status=document.getElementById('account-logout-status'); if (btn) { btn.disabled=true; btn.innerText='Dang xu ly...'; } if (status) { status.innerText='Dang xu ly...'; }">
                <button class="primary" type="submit">Dang xuat</button>
            </form>
            <span id="account-logout-status" class="sr-only" aria-live="polite"></span>
        </div>
    </div>

    <% if (form.getErrorMessage() != null) { %>
    <div class="notice error" role="alert"><%= HtmlEscaper.escape(form.getErrorMessage()) %></div>
    <% } %>
    <% if (form.getInfoMessage() != null) { %>
    <div class="notice info" role="status"><%= HtmlEscaper.escape(form.getInfoMessage()) %></div>
    <% } %>

    <% if (form.getBooks().isEmpty()) { %>
    <div class="empty-state panel">
        <p class="muted">Hien chua co sach nao dang mo ban.</p>
        <a class="link" href="<%= request.getContextPath() %>/account/cart.jsp">Xem gio hang</a>
    </div>
    <% } else { %>
    <div class="catalog-grid">
        <% for (CatalogBookView book : form.getBooks()) { %>
        <section class="panel catalog-card">
            <div>
                <h2><%= HtmlEscaper.escape(book.title().value()) %></h2>
                <div class="catalog-meta">
                    <span>Tac gia: <strong><%= HtmlEscaper.escape(book.author().value()) %></strong></span>
                    <span>ISBN: <strong><%= HtmlEscaper.escape(book.isbn().value()) %></strong></span>
                    <span>Gia: <strong><%= book.price().amount() %></strong></span>
                    <span>Ton kho: <strong><%= book.availableStock().value() %></strong></span>
                </div>
                <p class="catalog-description muted overflow-anywhere"><%= HtmlEscaper.escape(book.description().value()) %></p>
            </div>
            <form method="post" class="catalog-form" onsubmit="const btn=this.querySelector('button[type=submit]'); const status=document.getElementById('account-add-status-<%= book.bookId().value() %>'); if (btn && !btn.disabled) { btn.disabled=true; btn.innerText='Dang xu ly...'; } if (status) { status.innerText='Dang xu ly...'; }">
                <input type="hidden" name="bookId" value="<%= book.bookId().value() %>">
                <label for="quantity-<%= book.bookId().value() %>">So luong</label>
                <input id="quantity-<%= book.bookId().value() %>" class="purchase-input" type="number" min="1" name="quantity" value="<%= HtmlEscaper.escape(form.getQuantityValue(book.bookId().value())) %>" required <%= form.hasLineQuantityError(book.bookId().value()) ? "aria-invalid=\"true\" aria-describedby=\"account-line-quantity-error-" + book.bookId().value() + "\"" : "" %> <%= book.availableStock().value() <= 0 ? "disabled" : "" %>>
                <button class="primary" type="submit" <%= book.availableStock().value() <= 0 ? "disabled" : "" %>><%= book.availableStock().value() <= 0 ? "Het hang" : "Them vao gio" %></button>
                <% if (book.availableStock().value() <= 0) { %>
                <span class="catalog-out-of-stock">Tam het hang</span>
                <% } %>
                <% if (form.hasLineQuantityError(book.bookId().value())) { %>
                <div id="account-line-quantity-error-<%= book.bookId().value() %>" class="field-error"><%= HtmlEscaper.escape(form.getLineQuantityError()) %></div>
                <% } %>
                <span id="account-add-status-<%= book.bookId().value() %>" class="sr-only" aria-live="polite"></span>
            </form>
        </section>
        <% } %>
    </div>
    <% } %>
</main>
</body>
</html>
