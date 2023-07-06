package step.learning.filters;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import step.learning.services.LoadConfigService;
import step.learning.services.LoggerService;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Singleton
public class LogRequestFilter implements Filter {
    private FilterConfig filterConfig;

    @Inject
    private LoggerService loggerService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String url = ((HttpServletRequest)servletRequest).getRequestURL().toString();
        String queryString = ((HttpServletRequest)servletRequest).getQueryString();
        String method = ((HttpServletRequest)servletRequest).getMethod();
        url = url.substring(url.indexOf("app/") + 3);
        loggerService.log(method + " " + url + "?" + queryString + ", time: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), LoggerService.Status.INFO);
        //System.out.println(method + " " + url + "?" + queryString + ", time: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        filterConfig = null;
    }
}
