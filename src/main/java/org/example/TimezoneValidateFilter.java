package org.example;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.ZoneId;
import java.time.DateTimeException;

@WebFilter("/time")
public class TimezoneValidateFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String timezoneParam = request.getParameter("timezone");

        if (timezoneParam != null && !timezoneParam.isEmpty()) {
            try {

                ZoneId.of(timezoneParam);
            } catch (DateTimeException e) {

                HttpServletResponse httpResponse = (HttpServletResponse) response;
                httpResponse.setContentType("text/html;charset=UTF-8");
                httpResponse.getWriter().println("<html><body><h1>Invalid timezone</h1></body></html>");
                httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
        }

       chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
