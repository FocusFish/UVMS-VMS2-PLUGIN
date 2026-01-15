package fish.focus.uvms.plugins.vms2.rest;

import fish.focus.uvms.plugins.vms2.StartupBean;

import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(asyncSupported = true, urlPatterns = {"/*"})
public class UvmsPluginFilter implements Filter {

    @Inject
    StartupBean startupBean;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        if (!startupBean.isEnabled()) {
            HttpServletResponse response = (HttpServletResponse) res;
            response.sendError(503, "API is temporarily disabled");
            return;
        }

        chain.doFilter(request, res);
    }

    @Override
    public void destroy() {
    }
}
