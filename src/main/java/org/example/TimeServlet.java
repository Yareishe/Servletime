package org.example;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import java.io.IOException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/time")
public class TimeServlet extends HttpServlet {
    private static TemplateEngine engine;

    @Override
    public void init() {
        engine = new TemplateEngine();
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(getServletContext());
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setPrefix("/WEB-INF/templates/");
        templateResolver.setSuffix(".html");
        engine.setTemplateResolver(templateResolver);
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws  IOException {
        String timezone = req.getParameter("timezone");
        Map<String, Object> respMap = new LinkedHashMap<>();

        String lasTimezone = null;

        if(req.getCookies()!=null){
            for (Cookie cookie:req.getCookies()) {
                if (cookie.getName().equals("lasTimezone")){
                    lasTimezone = cookie.getValue();
                }
                log(cookie.getName() + "="+cookie.getValue());
            }

        }
        log(timezone);
        resp.setContentType("text/html");
        if (timezone == null || timezone.isEmpty()) {
            if(lasTimezone == null){
                respMap.put("timezone", getDate("UTC"));
            }
            else {
                respMap.put("timezone",getDate(lasTimezone));
            }
            //resp.addCookie(new Cookie("LasTimezone",null));
        }else {
            if (timezone.equals(lasTimezone)) {
                respMap.put("timezone", getDate(lasTimezone));
            }
            else {
                respMap.put("timezone", getDate(timezone));
            }
            resp.addCookie(new Cookie("lasTimezone",timezone));
        }

        Context simplecontext = new Context(req.getLocale(), respMap);
        engine.process("time", simplecontext, resp.getWriter());
        resp.getWriter().close();
    }

    public static String getDate(String param) {
        Date actualDate = new Date();
        if(param == null) {
            param = "UTC";}
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z").withZone(ZoneId.of(param));
        return dateFormat.format(actualDate.toInstant());
    }

}
