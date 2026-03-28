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
    <style>
        :root {
            color-scheme: light;
            --bg: #f4ecdf;
            --panel: #fffaf3;
            --border: #d9c7b1;
            --text: #2f271e;
            --muted: #6d6258;
            --accent: #8b4f29;
            --accent-strong: #6d3718;
            --on-accent: #fffaf3;
            --error-bg: #f9dfd8;
            --error-text: #842029;
            --info-bg: #e7f4ea;
            --info-text: #295b34;
            --panel-shadow: 0 18px 40px rgba(64, 44, 28, 0.12);
        }

        * {
            box-sizing: border-box;
        }

        body {
            margin: 0;
            min-height: 100vh;
            font-family: Georgia, "Times New Roman", serif;
            background:
                radial-gradient(circle at top, rgba(140, 79, 43, 0.2), transparent 32%),
                linear-gradient(135deg, #f8f3eb 0%, #efe2d1 100%);
            color: var(--text);
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 24px;
        }

        .panel {
            width: min(100%, 460px);
            background: var(--panel);
            border: 1px solid var(--border);
            border-radius: 20px;
            padding: 32px;
            box-shadow: var(--panel-shadow);
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

        .notice {
            border-radius: 12px;
            padding: 12px 14px;
            margin-bottom: 16px;
        }

        .notice.error {
            background: var(--error-bg);
            color: var(--error-text);
        }

        .notice.info {
            background: var(--info-bg);
            color: var(--info-text);
        }

        button {
            width: 100%;
            margin-top: 18px;
            border: 0;
            border-radius: 999px;
            min-height: 44px;
            padding: 13px 16px;
            font: inherit;
            font-weight: 700;
            color: var(--on-accent);
            background: linear-gradient(135deg, var(--accent), var(--accent-strong));
            cursor: pointer;
        }

        button:hover {
            filter: brightness(0.95);
        }

        button:disabled {
            opacity: 0.65;
            cursor: not-allowed;
        }

        .sr-only {
            position: absolute;
            width: 1px;
            height: 1px;
            padding: 0;
            margin: -1px;
            overflow: hidden;
            clip: rect(0, 0, 0, 0);
            white-space: nowrap;
            border: 0;
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

        a:focus-visible,
        button:focus-visible,
        input:focus-visible {
            outline: 2px solid var(--accent);
            outline-offset: 3px;
        }
    </style>
</head>
<body>
<main class="panel">
    <h1>Dang nhap</h1>
    <p>Truy cap khu vuc tai khoan cua ban de quan ly don hang va gio hang.</p>
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
