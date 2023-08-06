package org.example;



import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.zone.ZoneRulesException;
@WebFilter("/time")
public class TimezoneValidateFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest req,
                            HttpServletResponse resp,
                            FilterChain chain) throws IOException, ServletException {
        String timezone = req.getParameter("timezone");
        if (timezone == null || timezone.isEmpty()) {
            chain.doFilter(req, resp);
        } else {
            try {
                chain.doFilter(req, resp);
            } catch (ZoneRulesException e) {
                resp.setStatus(400);
                resp.getWriter().write("Invalid timezone");
                resp.getWriter().close();
            }
        }
    }
}