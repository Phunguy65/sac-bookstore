<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="io.github.phunguy65.bookstore.auth.interfaces.web.HtmlEscaper" %>
<%@ page import="io.github.phunguy65.bookstore.purchase.application.service.CartLineView" %>
<%@ page import="io.github.phunguy65.bookstore.purchase.interfaces.web.CheckoutPageBean" %>
<%@ page import="io.github.phunguy65.bookstore.purchase.interfaces.web.CheckoutPageModel" %>
<%@ page import="jakarta.enterprise.inject.spi.CDI" %>
<%@ include file="/WEB-INF/jspf/auth/require-auth.jspf" %>
<%
    CheckoutPageModel form = CDI.current().select(CheckoutPageBean.class).get().handle(request, response);
    if (response.isCommitted()) { return; }
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thanh toan - Bookstore</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/purchase-shared.css">
    <style>
        body { background:linear-gradient(150deg,#faf6f0 0%,#efe2d1 100%); }
        .shell { max-width:1020px; }
        .layout { display:grid; grid-template-columns:1.2fr .8fr; gap:22px; } .panel { border:1px solid var(--border); border-radius:20px; padding:20px; background:rgba(255,250,243,.7); }
        .field { display:flex; flex-direction:column; gap:6px; margin-top:12px; } .grid { display:grid; grid-template-columns:repeat(2,minmax(0,1fr)); gap:12px; }
        input { border-radius:12px; padding:10px 12px; }
        button { width:100%; margin-top:18px; }
        ul { padding-left:18px; }
        .summary, .item { overflow-wrap:anywhere; }
        @media (max-width: 820px) { .layout { grid-template-columns:1fr; } .grid { grid-template-columns:1fr; } }
    </style>
</head>
<body>
<main class="shell">
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

    <div class="layout">
        <form method="post" class="panel" onsubmit="const btn=this.querySelector('button[type=submit]'); const status=document.getElementById('checkout-submit-status'); if (btn) { btn.disabled=true; btn.innerText='Dang xu ly...'; } if (status) { status.innerText='Dang xu ly...'; }">
            <h2>Dia chi giao hang</h2>
            <p class="muted"><%= form.isRequiresShippingAddressInput() ? "Ban chua co dia chi mac dinh. Vui long nhap thong tin de luu va dat hang." : "Thong tin dang duoc dien tu dia chi mac dinh hien tai." %></p>
            <div class="grid">
                <div class="field"><label for="recipientName">Nguoi nhan</label><input id="recipientName" name="recipientName" autocomplete="name" required <%= form.isRequiresShippingAddressInput() ? "" : "readonly aria-readonly=\"true\"" %> <%= form.getFieldError("recipientName") != null ? "aria-invalid=\"true\" aria-describedby=\"recipientName-error\"" : "" %> value="<%= HtmlEscaper.escape(form.getAddressForm().getRecipientName()) %>"><% if (form.getFieldError("recipientName") != null) { %><div id="recipientName-error" class="field-error"><%= HtmlEscaper.escape(form.getFieldError("recipientName")) %></div><% } %></div>
                <div class="field"><label for="phoneNumber">So dien thoai</label><input id="phoneNumber" name="phoneNumber" autocomplete="tel" inputmode="tel" required <%= form.isRequiresShippingAddressInput() ? "" : "readonly aria-readonly=\"true\"" %> <%= form.getFieldError("phoneNumber") != null ? "aria-invalid=\"true\" aria-describedby=\"phoneNumber-error\"" : "" %> value="<%= HtmlEscaper.escape(form.getAddressForm().getPhoneNumber()) %>"><% if (form.getFieldError("phoneNumber") != null) { %><div id="phoneNumber-error" class="field-error"><%= HtmlEscaper.escape(form.getFieldError("phoneNumber")) %></div><% } %></div>
                <div class="field"><label for="line1">Dia chi</label><input id="line1" name="line1" autocomplete="address-line1" required <%= form.isRequiresShippingAddressInput() ? "" : "readonly aria-readonly=\"true\"" %> <%= form.getFieldError("line1") != null ? "aria-invalid=\"true\" aria-describedby=\"line1-error\"" : "" %> value="<%= HtmlEscaper.escape(form.getAddressForm().getLine1()) %>"><% if (form.getFieldError("line1") != null) { %><div id="line1-error" class="field-error"><%= HtmlEscaper.escape(form.getFieldError("line1")) %></div><% } %></div>
                <div class="field"><label for="line2">Dia chi bo sung</label><input id="line2" name="line2" <%= form.isRequiresShippingAddressInput() ? "" : "readonly aria-readonly=\"true\"" %> value="<%= HtmlEscaper.escape(form.getAddressForm().getLine2()) %>"></div>
                <div class="field"><label for="ward">Ward</label><input id="ward" name="ward" autocomplete="address-level4" required <%= form.isRequiresShippingAddressInput() ? "" : "readonly aria-readonly=\"true\"" %> <%= form.getFieldError("ward") != null ? "aria-invalid=\"true\" aria-describedby=\"ward-error\"" : "" %> value="<%= HtmlEscaper.escape(form.getAddressForm().getWard()) %>"><% if (form.getFieldError("ward") != null) { %><div id="ward-error" class="field-error"><%= HtmlEscaper.escape(form.getFieldError("ward")) %></div><% } %></div>
                <div class="field"><label for="district">District</label><input id="district" name="district" autocomplete="address-level3" required <%= form.isRequiresShippingAddressInput() ? "" : "readonly aria-readonly=\"true\"" %> <%= form.getFieldError("district") != null ? "aria-invalid=\"true\" aria-describedby=\"district-error\"" : "" %> value="<%= HtmlEscaper.escape(form.getAddressForm().getDistrict()) %>"><% if (form.getFieldError("district") != null) { %><div id="district-error" class="field-error"><%= HtmlEscaper.escape(form.getFieldError("district")) %></div><% } %></div>
                <div class="field"><label for="city">City</label><input id="city" name="city" autocomplete="address-level2" required <%= form.isRequiresShippingAddressInput() ? "" : "readonly aria-readonly=\"true\"" %> <%= form.getFieldError("city") != null ? "aria-invalid=\"true\" aria-describedby=\"city-error\"" : "" %> value="<%= HtmlEscaper.escape(form.getAddressForm().getCity()) %>"><% if (form.getFieldError("city") != null) { %><div id="city-error" class="field-error"><%= HtmlEscaper.escape(form.getFieldError("city")) %></div><% } %></div>
                <div class="field"><label for="province">Province</label><input id="province" name="province" autocomplete="address-level1" required <%= form.isRequiresShippingAddressInput() ? "" : "readonly aria-readonly=\"true\"" %> <%= form.getFieldError("province") != null ? "aria-invalid=\"true\" aria-describedby=\"province-error\"" : "" %> value="<%= HtmlEscaper.escape(form.getAddressForm().getProvince()) %>"><% if (form.getFieldError("province") != null) { %><div id="province-error" class="field-error"><%= HtmlEscaper.escape(form.getFieldError("province")) %></div><% } %></div>
                <div class="field"><label for="postalCode">Postal code</label><input id="postalCode" name="postalCode" autocomplete="postal-code" inputmode="numeric" required <%= form.isRequiresShippingAddressInput() ? "" : "readonly aria-readonly=\"true\"" %> <%= form.getFieldError("postalCode") != null ? "aria-invalid=\"true\" aria-describedby=\"postalCode-error\"" : "" %> value="<%= HtmlEscaper.escape(form.getAddressForm().getPostalCode()) %>"><% if (form.getFieldError("postalCode") != null) { %><div id="postalCode-error" class="field-error"><%= HtmlEscaper.escape(form.getFieldError("postalCode")) %></div><% } %></div>
            </div>
            <button type="submit">Dat don COD</button>
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
