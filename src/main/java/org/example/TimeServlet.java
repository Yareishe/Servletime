package org.example;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/time")
public class TimeServlet extends HttpServlet {
    private transient static TemplateEngine engine;
    @Override
    public void init() throws ServletException {
        engine = new TemplateEngine();
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(getServletContext());
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setPrefix("/WEB-INF/templates/");
        templateResolver.setSuffix(".html");
        engine.setTemplateResolver(templateResolver);
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String timezone = req.getParameter("timezone");
        Map<String, Object> respMap = new LinkedHashMap<>();

        String LasTimezone = null;

        if(req.getCookies()!=null){
            for (Cookie cookie:req.getCookies()) {
                if (cookie.getName().equals("LasTimezone")){
                    LasTimezone = cookie.getValue();
                }
                log(cookie.getName() + "="+cookie.getValue());
            }
        }
        log(timezone);
        resp.addCookie(new Cookie("LasTimezone",timezone));
        resp.setContentType("text/html");
        if (timezone == null || timezone.isEmpty()) {
            respMap.put("timezone",getDate("UTC"));
        } else if (LasTimezone.equals(timezone)) {
            respMap.put("timezone",getDate(LasTimezone));
        } else {
            respMap.put("timezone", getDate(timezone));
        }

        Context simplecontext = new Context(req.getLocale(), respMap);
        engine.process("time", simplecontext, resp.getWriter());
        resp.getWriter().close();
    }

    public static String getDate(String param) {
        Date actualDate = new Date();
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z").withZone(ZoneId.of(param));
        return dateFormat.format(actualDate.toInstant());
    }

}
