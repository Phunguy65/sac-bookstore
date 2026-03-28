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
    <style>
        :root { color-scheme: light; --bg:#f4ecdf; --panel:#fffaf3; --border:#d9c7b1; --text:#2f271e; --muted:#6d6258; --accent:#8b4f29; }
        body { margin:0; min-height:100vh; font-family:Georgia, "Times New Roman", serif; background:linear-gradient(180deg,#f9f5ef 0%,#f0e5d7 100%); color:var(--text); padding:24px; }
        .shell { max-width:920px; margin:0 auto; background:var(--panel); border:1px solid var(--border); border-radius:24px; padding:28px; }
        .item { border-top:1px solid var(--border); padding:14px 0; } .muted { color:var(--muted); }
        .item, p { overflow-wrap:anywhere; }
        a { min-height:44px; display:inline-flex; align-items:center; padding:12px 18px; border:1px solid var(--border); border-radius:999px; color:var(--text); text-decoration:none; }
        a:focus-visible { outline:2px solid var(--accent); outline-offset:3px; }
        .toolbar { display:flex; justify-content:space-between; gap:12px; flex-wrap:wrap; align-items:center; }
    </style>
</head>
<body>
<main class="shell">
    <div class="toolbar">
        <div>
            <h1>Don #<%= form.getOrder().orderId() %></h1>
            <p class="muted">Trang thai: <%= form.getOrder().status() %> - Tong tien: <%= form.getOrder().totalAmount() %></p>
        </div>
        <a href="<%= request.getContextPath() %>/account/orders.jsp">Quay lai lich su</a>
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
        <div class="item">
            <strong><%= HtmlEscaper.escape(item.bookTitle()) %></strong><br>
            <span class="muted">ISBN: <%= HtmlEscaper.escape(item.bookIsbn()) %> - So luong: <%= item.quantity() %> - Thanh tien: <%= item.lineTotal() %></span>
        </div>
        <% } %>
    </section>
</main>
</body>
</html>
