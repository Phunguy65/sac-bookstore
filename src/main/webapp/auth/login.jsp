<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="io.github.phunguy65.bookstore.auth.interfaces.web.HtmlEscaper" %>
<%@ page import="io.github.phunguy65.bookstore.auth.interfaces.web.LoginPageBean" %>
<%@ page import="io.github.phunguy65.bookstore.auth.interfaces.web.LoginPageModel" %>
<%@ page import="jakarta.enterprise.inject.spi.CDI" %>
<%
    LoginPageModel form = CDI.current().select(LoginPageBean.class).get().handle(request, response);
    if (response.isCommitted()) {
        return;
    }
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dang nhap - Bookstore</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/purchase-shared.css">
    <style>
        * {
            box-sizing: border-box;
        }

        body {
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 24px;
        }

        .panel {
            width: min(100%, 460px);
            padding: 32px;
        }

        h1 {
            margin: 0 0 8px;
            font-size: clamp(2rem, 5vw, 2.6rem);
        }

        p {
            margin: 0 0 20px;
            color: var(--muted);
            line-height: 1.5;
        }

        label {
            display: block;
            margin: 14px 0 6px;
            font-weight: 700;
        }

        input {
            width: 100%;
            min-height: 44px;
            padding: 12px 14px;
            border-radius: 12px;
            border: 1px solid var(--border);
            font: inherit;
            background: #fff;
        }

        input:focus {
            border-color: var(--accent);
        }

        button {
            width: 100%;
            margin-top: 18px;
            font-weight: 700;
            cursor: pointer;
        }

        button:hover {
            filter: brightness(0.95);
        }

        .links {
            margin-top: 18px;
            display: flex;
            justify-content: space-between;
            gap: 16px;
            flex-wrap: wrap;
        }

        a {
            color: var(--accent-strong);
            min-height: 44px;
            display: inline-flex;
            align-items: center;
        }

        a:hover {
            color: var(--accent);
        }

        .panel .notice {
            margin-bottom: 16px;
        }
    </style>
</head>
<body class="purchase-page purchase-page-soft">
<main class="panel">
    <h1>Dang nhap</h1>
    <p>Truy cap khu vuc tai khoan cua ban de quan ly don hang va gio hang.</p>
    <% if (form.isShowDevCredentials()) { %>
    <div class="notice info" role="status">
        <strong>Tai khoan dev:</strong><br>
        Email: <%= HtmlEscaper.escape(form.getDevEmail()) %><br>
        Mat khau: <%= HtmlEscaper.escape(form.getDevPassword()) %>
    </div>
    <% } %>
    <% if (form.getErrorMessage() != null) { %>
    <div class="notice error" role="alert"><%= HtmlEscaper.escape(form.getErrorMessage()) %></div>
    <% } %>
    <% if (form.getInfoMessage() != null) { %>
    <div class="notice info" role="alert"><%= HtmlEscaper.escape(form.getInfoMessage()) %></div>
    <% } %>
    <form method="post" action="" onsubmit="const btn=this.querySelector('button[type=submit]'); const status=document.getElementById('login-submit-status'); if (btn) { btn.disabled=true; btn.innerText='Dang xu ly...'; } if (status) { status.innerText='Dang xu ly...'; }">
        <label for="email">Email</label>
        <input id="email" name="email" type="email" autocomplete="email" required value="<%= HtmlEscaper.escape(form.getEmail()) %>">

        <label for="password">Mat khau</label>
        <input id="password" name="password" type="password" autocomplete="current-password" required>

        <button type="submit">Dang nhap</button>
    </form>
    <span id="login-submit-status" class="sr-only" aria-live="polite"></span>
    <div class="links">
        <a href="<%= request.getContextPath() %>/auth/register.jsp">Tao tai khoan moi</a>
        <a href="<%= request.getContextPath() %>/index.jsp">Quay ve trang chu</a>
    </div>
</main>
</body>
</html>
