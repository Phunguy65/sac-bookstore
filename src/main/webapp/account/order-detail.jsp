<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="io.github.phunguy65.bookstore.auth.interfaces.web.HtmlEscaper" %>
<%@ page import="io.github.phunguy65.bookstore.purchase.application.service.OrderItemDetailView" %>
<%@ page import="io.github.phunguy65.bookstore.purchase.interfaces.web.OrderDetailPageBean" %>
<%@ page import="io.github.phunguy65.bookstore.purchase.interfaces.web.OrderDetailPageModel" %>
<%@ page import="jakarta.enterprise.inject.spi.CDI" %>
<%@ include file="/WEB-INF/jspf/auth/require-auth.jspf" %>
<%
    OrderDetailPageModel form = CDI.current().select(OrderDetailPageBean.class).get().handle(request, response);
    if (response.isCommitted()) { return; }
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chi tiet don hang - Bookstore</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/purchase-shared.css">
    <style>
        p { overflow-wrap:anywhere; }
    </style>
</head>
<body class="purchase-page">
<main class="shell purchase-shell-narrow purchase-shell-shadow">
    <div class="toolbar">
        <div>
            <h1>Don #<%= form.getOrder().orderId() %></h1>
            <p class="muted">Trang thai: <%= form.getOrder().status() %> - Tong tien: <%= form.getOrder().totalAmount() %></p>
        </div>
        <a class="link" href="<%= request.getContextPath() %>/account/orders.jsp">Quay lai lich su</a>
    </div>

    <section>
        <h2>Dia chi giao hang</h2>
        <p>
            <strong><%= HtmlEscaper.escape(form.getOrder().shippingAddress().recipientName()) %></strong><br>
            <%= HtmlEscaper.escape(form.getOrder().shippingAddress().phoneNumber()) %><br>
            <%= HtmlEscaper.escape(form.getOrder().shippingAddress().line1()) %>
            <% if (form.getOrder().shippingAddress().line2() != null) { %>, <%= HtmlEscaper.escape(form.getOrder().shippingAddress().line2()) %><% } %><br>
            <%= HtmlEscaper.escape(form.getOrder().shippingAddress().ward()) %>,
            <%= HtmlEscaper.escape(form.getOrder().shippingAddress().district()) %>,
            <%= HtmlEscaper.escape(form.getOrder().shippingAddress().city()) %>,
            <%= HtmlEscaper.escape(form.getOrder().shippingAddress().province()) %>
            <%= HtmlEscaper.escape(form.getOrder().shippingAddress().postalCode()) %>
        </p>
    </section>

    <section>
        <h2>San pham</h2>
        <% for (OrderItemDetailView item : form.getOrder().items()) { %>
        <div class="purchase-item overflow-anywhere">
            <strong><%= HtmlEscaper.escape(item.bookTitle()) %></strong><br>
            <span class="muted">ISBN: <%= HtmlEscaper.escape(item.bookIsbn()) %> - So luong: <%= item.quantity() %> - Thanh tien: <%= item.lineTotal() %></span>
        </div>
        <% } %>
    </section>
</main>
</body>
</html>
