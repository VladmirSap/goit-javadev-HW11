package org.example;


import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;

@WebServlet("/time")
public class TimeServlet extends HttpServlet {

    private static final String DEFAULT_TIMEZONE = "UTC";
    private TemplateEngine templateEngine;

    @Override
    public void init() throws ServletException {

        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML");
        templateResolver.setCharacterEncoding("UTF-8");

        templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        String timezoneParam = request.getParameter("timezone");

        if (timezoneParam == null || timezoneParam.isEmpty()) {
            Cookie[] cookies = request.getCookies();
            timezoneParam = getTimezoneFromCookie(cookies);
        }

        if (timezoneParam == null || timezoneParam.isEmpty()) {
            timezoneParam = DEFAULT_TIMEZONE;
        }

        ZoneId zoneId = getZoneIdFromTimezone(timezoneParam);
        Instant now = Instant.now();
        String formattedTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(zoneId)
                .format(now);

        setTimezoneCookie(response, timezoneParam);

        Context context = new Context();
        context.setVariable("timezone", timezoneParam);
        context.setVariable("time", formattedTime);

        templateEngine.process("time", context, response.getWriter());
    }

    private String getTimezoneFromCookie(Cookie[] cookies) {
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("lastTimezone".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private void setTimezoneCookie(HttpServletResponse response, String timezone) {
        Cookie timezoneCookie = new Cookie("lastTimezone", timezone);
        timezoneCookie.setMaxAge(60 * 60 * 24 * 365);  // Термін дії cookie — 365 днів
        timezoneCookie.setPath("/");  // доступно на всьому сайті
        response.addCookie(timezoneCookie);
    }

    private ZoneId getZoneIdFromTimezone(String timezone) {
        try {
            return ZoneId.of(timezone);
        } catch (DateTimeException e) {

            System.out.println("Error parsing timezone ID: " + e.getMessage());
            return ZoneId.of("UTC");
        }
    }
}
