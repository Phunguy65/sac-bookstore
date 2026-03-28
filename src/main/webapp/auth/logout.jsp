<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="io.github.phunguy65.bookstore.auth.interfaces.web.LogoutPageBean" %>
<%@ page import="jakarta.enterprise.inject.spi.CDI" %>
<%
    CDI.current().select(LogoutPageBean.class).get().handle(request, response);
%>
