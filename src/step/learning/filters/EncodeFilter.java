package step.learning.filters;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import step.learning.services.RealPathService;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Singleton
public class EncodeFilter implements Filter {
    private FilterConfig filterConfig;

    @Inject
    private RealPathService realPathService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        realPathService.setRealPath(servletRequest.getServletContext().getRealPath("/"));
        servletRequest.setCharacterEncoding("UTF-8");
        servletResponse.setCharacterEncoding("UTF-8");
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        filterConfig = null;
    }
}
