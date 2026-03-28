<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="io.github.phunguy65.bookstore.auth.interfaces.web.AuthSession" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Bookstore</title>
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
            background:
                radial-gradient(circle at top left, rgba(139, 79, 41, 0.18), transparent 28%),
                linear-gradient(160deg, #f8f2e9 0%, #ecdfce 100%);
            color: var(--text);
            padding: 32px 20px;
        }

        .hero {
            max-width: 840px;
            margin: 0 auto;
            background: var(--panel);
            border: 1px solid var(--border);
            border-radius: 28px;
            padding: 36px;
            box-shadow: var(--panel-shadow);
        }

        h1 {
            margin: 0 0 12px;
            font-size: clamp(2.2rem, 5vw, 3.6rem);
        }

        p {
            margin: 0;
            color: var(--muted);
            line-height: 1.7;
            max-width: 62ch;
        }

        .actions {
            margin-top: 28px;
            display: flex;
            gap: 14px;
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
            text-decoration: none;
            font: inherit;
        }

        a.primary,
        button.primary {
            background: var(--accent);
            color: var(--on-accent);
            border: 0;
        }

        a.primary:hover,
        button.primary:hover {
            background: var(--accent-strong);
        }

        a.secondary {
            border: 1px solid var(--border);
            color: var(--text);
        }

        a.secondary:hover {
            border-color: var(--accent);
            color: var(--accent-strong);
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

        .status {
            margin-top: 22px;
            padding: 14px 16px;
            border-radius: 16px;
            background: rgba(47, 107, 96, 0.09);
            color: var(--text);
        }
    </style>
</head>
<body>
<main class="hero">
    <h1>Bookstore Web App</h1>
    <p>Kho auth nen cho web app da san sang: dang ky, dang nhap, dang xuat, quan ly gio hang, thanh toan COD va theo doi lich su don hang cua khach mua.</p>
    <div class="status">
        <% if (AuthSession.isAuthenticated(request)) { %>
        Ban dang dang nhap voi customer id <strong><%= AuthSession.getCustomerId(request).value() %></strong>.
        <% } else { %>
        Ban chua dang nhap. Hay tao tai khoan moi hoac dang nhap de tiep tuc.
        <% } %>
    </div>
    <div class="actions">
        <% if (AuthSession.isAuthenticated(request)) { %>
        <a class="primary" href="<%= request.getContextPath() %>/account/index.jsp">Mo trang tai khoan</a>
        <form method="post" action="<%= request.getContextPath() %>/auth/logout.jsp" onsubmit="const btn=this.querySelector('button[type=submit]'); const status=document.getElementById('home-logout-status'); if (btn) { btn.disabled=true; btn.innerText='Dang xu ly...'; } if (status) { status.innerText='Dang xu ly...'; }">
            <button class="primary" type="submit">Dang xuat</button>
        </form>
        <span id="home-logout-status" class="sr-only" aria-live="polite"></span>
        <% } else { %>
        <a class="primary" href="<%= request.getContextPath() %>/auth/login.jsp">Dang nhap</a>
        <a class="secondary" href="<%= request.getContextPath() %>/auth/register.jsp">Dang ky</a>
        <% } %>
    </div>
</main>
</body>
</html>
