package step.learning.filters;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import step.learning.services.DataService;
import step.learning.services.MysqlDataService;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.Connection;

@Singleton
public class DataFilter implements Filter {
    private FilterConfig filterConfig;

    @Inject
    private DataService dataService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        String localUrl = request.getServletPath();

        if(localUrl.endsWith(".png")){
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        Connection connection = dataService.getConnection();
        if(connection == null){
            servletRequest.getRequestDispatcher("/WEB-INF/static.jsp").forward(servletRequest, servletResponse);
        } else{
            servletRequest.setAttribute("DataService", dataService);
            filterChain.doFilter(servletRequest, servletResponse);
        }

    }

    @Override
    public void destroy() {
        filterConfig = null;
    }
}