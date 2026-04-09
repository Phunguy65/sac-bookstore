<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="io.github.phunguy65.bookstore.shared.interfaces.web.HtmlEscaper" %>
<%@ page import="io.github.phunguy65.bookstore.purchase.application.service.CartLineView" %>
<%@ page import="io.github.phunguy65.bookstore.purchase.interfaces.web.CheckoutPage" %>
<%@ page import="io.github.phunguy65.bookstore.purchase.interfaces.web.CheckoutPageRequest" %>
<%@ page import="io.github.phunguy65.bookstore.purchase.interfaces.web.CheckoutPageResult" %>
<%@ page import="io.github.phunguy65.bookstore.purchase.interfaces.web.CheckoutPageModel" %>
<%@ page import="io.github.phunguy65.bookstore.purchase.interfaces.web.PageAction" %>
<%@ page import="javax.naming.InitialContext" %>
<%!
    private CheckoutPage checkoutPage = null;

    public void jspInit() {
        try {
            InitialContext ic = new InitialContext();
            checkoutPage = (CheckoutPage) ic.lookup("java:module/CheckoutPageBean!io.github.phunguy65.bookstore.purchase.interfaces.web.CheckoutPage");
        } catch (Exception ex) {
            System.out.println("Could not create CheckoutPage bean. " + ex.getMessage());
        }
    }

    public void jspDestroy() {
        checkoutPage = null;
    }
%>
<%
    CheckoutPageRequest pageRequest = new CheckoutPageRequest(
        request.getMethod(),
        request.getParameter("recipientName"),
        request.getParameter("phoneNumber"),
        request.getParameter("line1"),
        request.getParameter("line2"),
        request.getParameter("ward"),
        request.getParameter("district"),
        request.getParameter("city"),
        request.getParameter("province"),
        request.getParameter("postalCode")
    );
    CheckoutPageResult result = checkoutPage.handle(pageRequest);
    if (result.action() == PageAction.REDIRECT) {
        response.sendRedirect(request.getContextPath() + result.redirectUrl());
        return;
    }
    CheckoutPageModel form = result.model();
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thanh toan - Bookstore</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/purchase-shared.css">
    <style>
        button { width:100%; margin-top:18px; }
        ul { padding-left:18px; }
        .summary, .item { overflow-wrap:anywhere; }
    </style>
</head>
<body class="purchase-page purchase-page-soft">
<main class="shell purchase-shell-wide purchase-shell-shadow">
    <div class="toolbar">
        <div>
            <h1>Thanh toan COD</h1>
            <p class="muted">Xac nhan toan bo gio hang va dia chi giao hang cua ban.</p>
        </div>
        <a class="link" href="<%= request.getContextPath() %>/account/cart.jsp">Quay lai gio hang</a>
    </div>

    <% if (form.getErrorMessage() != null) { %>
    <div class="notice error" role="alert"><%= HtmlEscaper.escape(form.getErrorMessage()) %></div>
    <% } %>

    <div class="purchase-layout">
        <form method="post" class="panel" onsubmit="const btn=this.querySelector('button[type=submit]'); const status=document.getElementById('checkout-submit-status'); if (btn) { btn.disabled=true; btn.innerText='Dang xu ly...'; } if (status) { status.innerText='Dang xu ly...'; }">
            <h2>Dia chi giao hang</h2>
            <p class="muted"><%= form.isRequiresShippingAddressInput() ? "Ban chua co dia chi mac dinh. Vui long nhap thong tin de luu va dat hang." : "Thong tin dang duoc dien tu dia chi mac dinh hien tai." %></p>
            <div class="grid-two">
                <div class="field"><label for="recipientName">Nguoi nhan</label><input id="recipientName" class="purchase-input" name="recipientName" autocomplete="name" required <%= form.isRequiresShippingAddressInput() ? "" : "readonly aria-readonly=\"true\"" %> <%= form.getFieldError("recipientName") != null ? "aria-invalid=\"true\" aria-describedby=\"recipientName-error\"" : "" %> value="<%= HtmlEscaper.escape(form.getAddressForm().getRecipientName()) %>"><% if (form.getFieldError("recipientName") != null) { %><div id="recipientName-error" class="field-error"><%= HtmlEscaper.escape(form.getFieldError("recipientName")) %></div><% } %></div>
                <div class="field"><label for="phoneNumber">So dien thoai</label><input id="phoneNumber" class="purchase-input" name="phoneNumber" autocomplete="tel" inputmode="tel" required <%= form.isRequiresShippingAddressInput() ? "" : "readonly aria-readonly=\"true\"" %> <%= form.getFieldError("phoneNumber") != null ? "aria-invalid=\"true\" aria-describedby=\"phoneNumber-error\"" : "" %> value="<%= HtmlEscaper.escape(form.getAddressForm().getPhoneNumber()) %>"><% if (form.getFieldError("phoneNumber") != null) { %><div id="phoneNumber-error" class="field-error"><%= HtmlEscaper.escape(form.getFieldError("phoneNumber")) %></div><% } %></div>
                <div class="field"><label for="line1">Dia chi</label><input id="line1" class="purchase-input" name="line1" autocomplete="address-line1" required <%= form.isRequiresShippingAddressInput() ? "" : "readonly aria-readonly=\"true\"" %> <%= form.getFieldError("line1") != null ? "aria-invalid=\"true\" aria-describedby=\"line1-error\"" : "" %> value="<%= HtmlEscaper.escape(form.getAddressForm().getLine1()) %>"><% if (form.getFieldError("line1") != null) { %><div id="line1-error" class="field-error"><%= HtmlEscaper.escape(form.getFieldError("line1")) %></div><% } %></div>
                <div class="field"><label for="line2">Dia chi bo sung</label><input id="line2" class="purchase-input" name="line2" <%= form.isRequiresShippingAddressInput() ? "" : "readonly aria-readonly=\"true\"" %> value="<%= HtmlEscaper.escape(form.getAddressForm().getLine2()) %>"></div>
                <div class="field"><label for="ward">Phuong/Xa</label><input id="ward" class="purchase-input" name="ward" autocomplete="address-level4" required <%= form.isRequiresShippingAddressInput() ? "" : "readonly aria-readonly=\"true\"" %> <%= form.getFieldError("ward") != null ? "aria-invalid=\"true\" aria-describedby=\"ward-error\"" : "" %> value="<%= HtmlEscaper.escape(form.getAddressForm().getWard()) %>"><% if (form.getFieldError("ward") != null) { %><div id="ward-error" class="field-error"><%= HtmlEscaper.escape(form.getFieldError("ward")) %></div><% } %></div>
                <div class="field"><label for="district">Quan/Huyen</label><input id="district" class="purchase-input" name="district" autocomplete="address-level3" required <%= form.isRequiresShippingAddressInput() ? "" : "readonly aria-readonly=\"true\"" %> <%= form.getFieldError("district") != null ? "aria-invalid=\"true\" aria-describedby=\"district-error\"" : "" %> value="<%= HtmlEscaper.escape(form.getAddressForm().getDistrict()) %>"><% if (form.getFieldError("district") != null) { %><div id="district-error" class="field-error"><%= HtmlEscaper.escape(form.getFieldError("district")) %></div><% } %></div>
                <div class="field"><label for="city">Thanh pho</label><input id="city" class="purchase-input" name="city" autocomplete="address-level2" required <%= form.isRequiresShippingAddressInput() ? "" : "readonly aria-readonly=\"true\"" %> <%= form.getFieldError("city") != null ? "aria-invalid=\"true\" aria-describedby=\"city-error\"" : "" %> value="<%= HtmlEscaper.escape(form.getAddressForm().getCity()) %>"><% if (form.getFieldError("city") != null) { %><div id="city-error" class="field-error"><%= HtmlEscaper.escape(form.getFieldError("city")) %></div><% } %></div>
                <div class="field"><label for="province">Tinh/Thanh</label><input id="province" class="purchase-input" name="province" autocomplete="address-level1" required <%= form.isRequiresShippingAddressInput() ? "" : "readonly aria-readonly=\"true\"" %> <%= form.getFieldError("province") != null ? "aria-invalid=\"true\" aria-describedby=\"province-error\"" : "" %> value="<%= HtmlEscaper.escape(form.getAddressForm().getProvince()) %>"><% if (form.getFieldError("province") != null) { %><div id="province-error" class="field-error"><%= HtmlEscaper.escape(form.getFieldError("province")) %></div><% } %></div>
                <div class="field"><label for="postalCode">Ma buu chinh</label><input id="postalCode" class="purchase-input" name="postalCode" autocomplete="postal-code" inputmode="numeric" required <%= form.isRequiresShippingAddressInput() ? "" : "readonly aria-readonly=\"true\"" %> <%= form.getFieldError("postalCode") != null ? "aria-invalid=\"true\" aria-describedby=\"postalCode-error\"" : "" %> value="<%= HtmlEscaper.escape(form.getAddressForm().getPostalCode()) %>"><% if (form.getFieldError("postalCode") != null) { %><div id="postalCode-error" class="field-error"><%= HtmlEscaper.escape(form.getFieldError("postalCode")) %></div><% } %></div>
            </div>
            <button class="primary" type="submit">Dat don COD</button>
            <span id="checkout-submit-status" class="sr-only" aria-live="polite"></span>
        </form>
        <section class="panel summary">
            <h2>Tom tat don hang</h2>
            <ul>
                <% for (CartLineView item : form.getCart().items()) { %>
                <li class="item"><strong><%= HtmlEscaper.escape(item.bookTitle().value()) %></strong> x <%= item.quantity().value() %> - <%= item.lineTotal().amount() %></li>
                <% } %>
            </ul>
            <p><strong>Tong cong: <%= form.getCart().totalAmount().amount() %></strong></p>
            <p class="muted">Don hang se duoc tao ngay ma khong can cong thanh toan truc tuyen.</p>
        </section>
    </div>
</main>
</body>
</html>
