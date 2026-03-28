<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="io.github.phunguy65.bookstore.auth.interfaces.web.HtmlEscaper" %>
<%@ page import="io.github.phunguy65.bookstore.purchase.application.service.OrderSummaryView" %>
<%@ page import="io.github.phunguy65.bookstore.purchase.interfaces.web.OrderHistoryPageBean" %>
<%@ page import="io.github.phunguy65.bookstore.purchase.interfaces.web.OrderHistoryPageModel" %>
<%@ page import="jakarta.enterprise.inject.spi.CDI" %>
<%@ include file="/WEB-INF/jspf/auth/require-auth.jspf" %>
<%
    OrderHistoryPageModel form = CDI.current().select(OrderHistoryPageBean.class).get().handle(request, response);
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Don hang cua toi - Bookstore</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/purchase-shared.css">
    <style>
        body { background:linear-gradient(180deg,#f9f5ef 0%,#f0e5d7 100%); }
        .shell { max-width:920px; }
        .order { border-top:1px solid var(--border); padding:18px 0; display:flex; justify-content:space-between; gap:12px; flex-wrap:wrap; }
        .order { overflow-wrap:anywhere; }
        .pagination { margin-top:20px; }
    </style>
</head>
<body>
<main class="shell">
    <div class="toolbar">
        <div>
            <h1>Lich su don hang</h1>
            <p class="muted">Tat ca don hang thuoc tai khoan hien tai.</p>
        </div>
        <a class="link" href="<%= request.getContextPath() %>/account/index.jsp">Ve tai khoan</a>
    </div>
    <% if (form.getErrorMessage() != null) { %>
    <div class="notice error" role="alert"><%= HtmlEscaper.escape(form.getErrorMessage()) %></div>
    <% } %>
    <% if (form.getOrders().isEmpty()) { %>
    <div class="empty-state">
        <p class="muted">Ban chua co don hang nao.</p>
        <a class="link" href="<%= request.getContextPath() %>/account/cart.jsp">Mo gio hang</a>
    </div>
    <% } else { %>
    <% for (OrderSummaryView order : form.getOrders()) { %>
    <div class="order">
        <div>
            <strong>Don #<%= order.orderId().value() %></strong><br>
            <span class="muted">Trang thai: <%= order.status() %> - <%= order.itemCount() %> san pham</span>
        </div>
        <div>
            <strong><%= order.totalAmount().amount() %></strong><br>
            <a class="link" href="<%= request.getContextPath() %>/account/order-detail.jsp?orderId=<%= order.orderId().value() %>">Xem chi tiet</a>
        </div>
    </div>
    <% } %>
    <% } %>
    <% if (form.getTotalPages() > 1) { %>
    <nav class="actions pagination" aria-label="Order history pagination">
        <% if (form.isHasPreviousPage()) { %>
        <a class="link" href="<%= request.getContextPath() %>/account/orders.jsp?page=<%= form.getCurrentPage() - 1 %>">Trang truoc</a>
        <% } %>
        <span class="muted" aria-current="page">Trang <%= form.getCurrentPage() %>/<%= form.getTotalPages() %></span>
        <% if (form.isHasNextPage()) { %>
        <a class="link" href="<%= request.getContextPath() %>/account/orders.jsp?page=<%= form.getCurrentPage() + 1 %>">Trang sau</a>
        <% } %>
    </nav>
    <% } %>
</main>
</body>
</html>
