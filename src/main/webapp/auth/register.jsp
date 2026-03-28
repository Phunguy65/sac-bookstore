<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="io.github.phunguy65.bookstore.auth.interfaces.web.HtmlEscaper" %>
<%@ page import="io.github.phunguy65.bookstore.auth.interfaces.web.RegisterPageBean" %>
<%@ page import="io.github.phunguy65.bookstore.auth.interfaces.web.RegisterPageModel" %>
<%@ page import="jakarta.enterprise.inject.spi.CDI" %>
<%
    RegisterPageModel form = CDI.current().select(RegisterPageBean.class).get().handle(request, response);
    if (response.isCommitted()) {
        return;
    }
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dang ky - Bookstore</title>
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
                radial-gradient(circle at bottom right, rgba(37, 101, 91, 0.2), transparent 30%),
                linear-gradient(135deg, #faf6f0 0%, #efe6d8 100%);
            color: var(--text);
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 24px;
        }

        .panel {
            width: min(100%, 540px);
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

        .grid {
            display: grid;
            grid-template-columns: repeat(2, minmax(0, 1fr));
            gap: 14px;
        }

        .field,
        .field-full {
            display: flex;
            flex-direction: column;
            gap: 6px;
        }

        .field-full {
            grid-column: 1 / -1;
        }

        label {
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

        .notice.error {
            border-radius: 12px;
            padding: 12px 14px;
            margin-bottom: 16px;
            background: var(--error-bg);
            color: var(--error-text);
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

        @media (max-width: 640px) {
            .grid {
                grid-template-columns: 1fr;
            }
        }
    </style>
</head>
<body>
<main class="panel">
    <h1>Tao tai khoan</h1>
    <p>Dang ky nhanh de luu gio hang, dat sach va xem lich su mua hang sau nay.</p>
    <% if (form.getErrorMessage() != null) { %>
    <div class="notice error" role="alert"><%= HtmlEscaper.escape(form.getErrorMessage()) %></div>
    <% } %>
    <form method="post" action="" onsubmit="const btn=this.querySelector('button[type=submit]'); const status=document.getElementById('register-submit-status'); if (btn) { btn.disabled=true; btn.innerText='Dang xu ly...'; } if (status) { status.innerText='Dang xu ly...'; }">
        <div class="grid">
            <div class="field-full">
                <label for="email">Email</label>
                <input id="email" name="email" type="email" autocomplete="email" required value="<%= HtmlEscaper.escape(form.getEmail()) %>">
            </div>
            <div class="field-full">
                <label for="fullName">Ho va ten</label>
                <input id="fullName" name="fullName" type="text" autocomplete="name" required value="<%= HtmlEscaper.escape(form.getFullName()) %>">
            </div>
            <div class="field">
                <label for="phoneNumber">So dien thoai</label>
                <input id="phoneNumber" name="phoneNumber" type="tel" autocomplete="tel" required value="<%= HtmlEscaper.escape(form.getPhoneNumber()) %>">
            </div>
            <div class="field">
                <label for="password">Mat khau</label>
                <input id="password" name="password" type="password" autocomplete="new-password" required>
            </div>
            <div class="field-full">
                <label for="confirmPassword">Xac nhan mat khau</label>
                <input id="confirmPassword" name="confirmPassword" type="password" autocomplete="new-password" required>
            </div>
        </div>
        <button type="submit">Dang ky</button>
    </form>
    <span id="register-submit-status" class="sr-only" aria-live="polite"></span>
    <div class="links">
        <a href="<%= request.getContextPath() %>/auth/login.jsp">Da co tai khoan?</a>
        <a href="<%= request.getContextPath() %>/index.jsp">Quay ve trang chu</a>
    </div>
</main>
</body>
</html>
