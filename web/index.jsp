<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // Si ya hay sesion activa, ir al menu; si no, al login
    if (session.getAttribute("usuario") != null) {
        response.sendRedirect("menu.jsp");
    } else {
        response.sendRedirect("login.jsp");
    }
%>
