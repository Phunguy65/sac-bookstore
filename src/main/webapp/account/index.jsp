<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="io.github.phunguy65.bookstore.auth.interfaces.web.AuthSession" %>
<%@ include file="/WEB-INF/jspf/auth/require-auth.jspf" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tai khoan - Bookstore</title>
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
            --panel-shadow: 0 18px 40px rgba(64, 44, 28, 0.12);
        }

        body {
            margin: 0;
            min-height: 100vh;
            font-family: Georgia, "Times New Roman", serif;
            background: linear-gradient(180deg, #f9f5ef 0%, #f0e5d7 100%);
            color: var(--text);
            padding: 32px 20px;
        }

        .shell {
            max-width: 760px;
            margin: 0 auto;
            background: var(--panel);
            border: 1px solid var(--border);
            border-radius: 24px;
            padding: 28px;
            box-shadow: var(--panel-shadow);
        }

        h1 {
            margin-top: 0;
        }

        .meta {
            color: var(--muted);
            line-height: 1.6;
        }

        .actions {
            margin-top: 24px;
            display: flex;
            gap: 12px;
            flex-wrap: wrap;
        }

        a,
        button {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            border-radius: 999px;
            min-height: 44px;
            padding: 12px 18px;
            font: inherit;
        }

        a {
            text-decoration: none;
            border: 1px solid var(--border);
            color: var(--text);
        }

        button {
            border: 0;
            background: var(--accent);
            color: var(--on-accent);
            cursor: pointer;
        }

        a:hover {
            border-color: var(--accent);
            color: var(--accent-strong);
        }

        button:hover {
            background: var(--accent-strong);
        }

        a:focus-visible,
        button:focus-visible {
            outline: 2px solid var(--accent);
            outline-offset: 3px;
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
    </style>
</head>
<body>
<main class="shell">
    <h1>Xin chao ban</h1>
    <p class="meta">
        Ban da dang nhap thanh cong. Session hien dang luu customer id:
        <strong><%= AuthSession.getCustomerId(request).value() %></strong>
    </p>
    <p class="meta">Tu day ban co the mo gio hang, thanh toan don COD va theo doi lich su mua hang cua minh.</p>
    <div class="actions">
        <a href="<%= request.getContextPath() %>/index.jsp">Ve trang chu</a>
        <a href="<%= request.getContextPath() %>/account/cart.jsp">Mo gio hang</a>
        <a href="<%= request.getContextPath() %>/account/orders.jsp">Xem don hang</a>
        <form method="post" action="<%= request.getContextPath() %>/auth/logout.jsp" onsubmit="const btn=this.querySelector('button[type=submit]'); const status=document.getElementById('account-logout-status'); if (btn) { btn.disabled=true; btn.innerText='Dang xu ly...'; } if (status) { status.innerText='Dang xu ly...'; }">
            <button type="submit">Dang xuat</button>
        </form>
        <span id="account-logout-status" class="sr-only" aria-live="polite"></span>
    </div>
</main>
</body>
</html>
